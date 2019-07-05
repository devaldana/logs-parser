package com.wallethub.config;

import com.wallethub.batch.mappers.RequestMapper;
import com.wallethub.domain.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;

import static com.wallethub.util.Global.LOG_FILE_DELIMITER;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${accesslog}")
    private String accessLogFilePath;

    @Autowired
    public BatchConfig(final JobBuilderFactory jobBuilderFactory,
                       final StepBuilderFactory stepBuilderFactory,
                       final EntityManagerFactory entityManagerFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public FlatFileItemReader<Request> reader() {
        final FlatFileItemReader<Request> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(accessLogFilePath));
        final DefaultLineMapper<Request> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(LOG_FILE_DELIMITER));
        lineMapper.setFieldSetMapper(new RequestMapper());
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public JpaItemWriter<Request> writer() {
        final JpaItemWriter<Request> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                                 .<Request, Request>chunk(10000)
                                 .reader(reader())
                                 .writer(writer())
                                 .build();
    }

    @Bean
    public Job importLogsJob() {
        return jobBuilderFactory.get("importLogsJob")
                                .incrementer(new RunIdIncrementer())
                                .flow(step1())
                                .end().build();
    }
}