package com.wallethub.config;

import com.wallethub.batch.helpers.JobHelper;
import com.wallethub.batch.mappers.RequestMapper;
import com.wallethub.batch.providers.RequestQueryProvider;
import com.wallethub.domain.BlockedIp;
import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;

import static com.wallethub.util.Global.LOG_FILE_DELIMITER;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ArgumentsData argumentsData;
    private final EntityManagerFactory entityManagerFactory;
    private final RequestQueryProvider queryProvider;

    public BatchConfig(final JobBuilderFactory jobBuilderFactory,
                       final StepBuilderFactory stepBuilderFactory,
                       final EntityManagerFactory entityManagerFactory,
                       final RequestQueryProvider  queryProvider,
                       final ArgumentsData argumentsData) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.queryProvider = queryProvider;
        this.argumentsData = argumentsData;
    }

    @Bean
    public Job importLogsJob() {
        return jobBuilderFactory.get("importLogsJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .build();
    }

    public Step step1() {
        return stepBuilderFactory.get("loadAllRequestsToDatabase")
                .<Request, Request>chunk(100000)
                .reader(logFileReader())
                .writer(requestJpaWriter())
                .build();
    }

    public Step step2() {
        return stepBuilderFactory.get("retrieveFilteredRequests")
                .<BlockedIp, BlockedIp>chunk(1000000)
                .reader(blockedIpJpaReader())
                .processor(blockedIpProcessor())
                .writer(compositeItemWriter())
                .build();
    }

    public FlatFileItemReader<Request> logFileReader() {
        final FlatFileItemReader<Request> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(argumentsData.getAccessLogFilePath()));
        final DefaultLineMapper<Request> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(LOG_FILE_DELIMITER));
        lineMapper.setFieldSetMapper(new RequestMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    public JpaItemWriter<Request> requestJpaWriter() {
        final JpaItemWriter<Request> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    public JpaPagingItemReader<BlockedIp> blockedIpJpaReader() {
        return new JpaPagingItemReaderBuilder<BlockedIp>()
                .name("jpaReader")
                .entityManagerFactory(entityManagerFactory)
                .queryProvider(queryProvider)
                .pageSize(100000)
                .build();
    }

    public ItemProcessor<BlockedIp, BlockedIp> blockedIpProcessor() {
        return (blockedIp) -> {
            blockedIp.setId(null);
            blockedIp.setMessage(JobHelper.getBlockedIpMessage(blockedIp, argumentsData));
            return blockedIp;
        };
    }



    public CompositeItemWriter<BlockedIp> compositeItemWriter() {
        CompositeItemWriter<BlockedIp> writer = new CompositeItemWriter<>();
        writer.setDelegates(Arrays.asList(consoleBlockedIpWriter(), blockedIpJpaItemWriter()));
        return writer;
    }

    public ItemWriter<BlockedIp> consoleBlockedIpWriter() {
        return (items) -> items.forEach(blockedIp -> log.info(blockedIp.toString()));
    }


    public JpaItemWriter<BlockedIp> blockedIpJpaItemWriter() {
        final JpaItemWriter<BlockedIp> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }




}