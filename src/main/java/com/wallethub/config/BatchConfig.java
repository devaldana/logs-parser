package com.wallethub.config;

import com.wallethub.batch.mappers.RequestMapper;
import com.wallethub.batch.writers.RequestWriter;
import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import static com.wallethub.util.Global.LOG_FILE_DELIMITER;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final RequestWriter requestWriter;
    private final ArgumentsData argumentsData;

    @Autowired
    public BatchConfig(final JobBuilderFactory jobBuilderFactory,
                       final StepBuilderFactory stepBuilderFactory,
                       final RequestWriter requestWriter,
                       final ArgumentsData argumentsData) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.requestWriter = requestWriter;
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
    public Step step1() {
        return stepBuilderFactory.get("step1")
                                 .<Request, Request>chunk(1000)
                                 .reader(reader())
                                 .writer(requestWriter)
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