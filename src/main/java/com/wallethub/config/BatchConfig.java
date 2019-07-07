package com.wallethub.config;

import com.wallethub.batch.steps.StepsBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepsBuilder stepsBuilder;

    @Bean
    public Job importLogsJob() {
        return jobBuilderFactory.get("importLogsJob")
                .incrementer(new RunIdIncrementer())
                .start(stepsBuilder.step1())
                .next(stepsBuilder.step2())
                .build();
    }
}