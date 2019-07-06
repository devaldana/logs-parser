package com.wallethub.config;

import com.wallethub.batch.mappers.RequestMapper;
import com.wallethub.batch.providers.RequestQueryProvider;
import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.util.List;

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
    public FlatFileItemReader<Request> reader() {
        final FlatFileItemReader<Request> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(argumentsData.getAccessLogFilePath()));
        final DefaultLineMapper<Request> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(LOG_FILE_DELIMITER));
        lineMapper.setFieldSetMapper(new RequestMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public JpaPagingItemReader<Request> itemReader() {
        return new JpaPagingItemReaderBuilder<Request>()
                .name("jpaReader")
                .entityManagerFactory(entityManagerFactory)
                .queryProvider(queryProvider)
                .pageSize(1000)
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("retrieveFilteredRequests")
                .<Request, Request>chunk(1000)
                .reader(itemReader())
                .writer(new RequestWriter())
                .build();
    }

    class RequestWriter implements ItemWriter<Request> {

        @Override
        public void write(List<? extends Request> list) throws Exception {
            list.stream().forEach(System.out::println);
        }
    }

    @Bean("requestJpaItemWriter")
    public JpaItemWriter<Request> requestJpaItemWriter() {
        final JpaItemWriter<Request> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("loadAllRequestsToDatabase")
                                 .<Request, Request>chunk(10000)
                                 .reader(reader())
                                 .writer(requestJpaItemWriter())
                                 .build();
    }

    @Bean
    public Job importLogsJob() {
        return jobBuilderFactory.get("importLogsJob")
                                .incrementer(new RunIdIncrementer())
                                .start(step1())
                                .next(step2())
                                .build();
    }
}