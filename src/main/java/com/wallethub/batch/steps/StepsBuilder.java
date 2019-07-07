package com.wallethub.batch.steps;

import com.wallethub.batch.helpers.JobHelper;
import com.wallethub.batch.mappers.RequestMapper;
import com.wallethub.batch.providers.RequestQueryProvider;
import com.wallethub.domain.BlockedIp;
import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

import static com.wallethub.util.Global.LOG_FILE_DELIMITER;

@Slf4j
@Component
@AllArgsConstructor
public class StepsBuilder {

    private final StepBuilderFactory stepBuilderFactory;
    private final ArgumentsData argumentsData;
    private final EntityManagerFactory entityManagerFactory;
    private final RequestQueryProvider queryProvider;

    /*
     * = = = = = = = = = = = = = = STEP 1 CONFIG  = = = = = = = = = = = = = =
     *
     * Step to load all requests from log file to database
     *
     */
    public Step step1() {
        return stepBuilderFactory.get("loadAllRequestsToDatabase")
                .<Request, Request>chunk(100000)
                .reader(logFileReader())
                .writer(requestJpaWriter())
                .build();
    }

    private FlatFileItemReader<Request> logFileReader() {
        final FlatFileItemReader<Request> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(argumentsData.getAccessLogFilePath()));
        final DefaultLineMapper<Request> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(LOG_FILE_DELIMITER));
        lineMapper.setFieldSetMapper(new RequestMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    private JpaItemWriter<Request> requestJpaWriter() {
        final JpaItemWriter<Request> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    /*
     * = = = = = = = = = = = = = = STEP 2 CONFIG  = = = = = = = = = = = = = =
     *
     * Step to load all requests from database which meets the conditions of blocked
     *
     */
    public Step step2() {
        return stepBuilderFactory.get("loadBlockedIps")
                .<BlockedIp, BlockedIp>chunk(1000000)
                .reader(blockedIpJpaReader())
                .processor(blockedIpProcessor())
                .writer(blockedIpWriter())
                .build();
    }

    private JpaPagingItemReader<BlockedIp> blockedIpJpaReader() {
        return new JpaPagingItemReaderBuilder<BlockedIp>()
                .name("blockedIpJpaReader")
                .entityManagerFactory(entityManagerFactory)
                .queryProvider(queryProvider)
                .pageSize(100000)
                .build();
    }

    private ItemProcessor<BlockedIp, BlockedIp> blockedIpProcessor() {
        return (blockedIp) -> {
            blockedIp.setId(null);
            blockedIp.setMessage(JobHelper.getBlockedIpMessage(blockedIp, argumentsData));
            return blockedIp;
        };
    }

    private CompositeItemWriter<BlockedIp> blockedIpWriter() {
        final CompositeItemWriter<BlockedIp> writer = new CompositeItemWriter<>();
        writer.setDelegates(Arrays.asList(blockedIpConsoleWriter(), blockedIpJpaWriter()));
        return writer;
    }

    private ItemWriter<BlockedIp> blockedIpConsoleWriter() {
        return (items) -> items.forEach(blockedIp -> log.info(blockedIp.toString()));
    }

    private JpaItemWriter<BlockedIp> blockedIpJpaWriter() {
        final JpaItemWriter<BlockedIp> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    //TODO: write header to beauty blockedIpConsoleWriter output
}
