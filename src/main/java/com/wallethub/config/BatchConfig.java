package com.wallethub.config;

import com.wallethub.batch.builders.StepsBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author David Aldana
 * @since 2019.07
 */
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
                .listener(jobExecutionListener())
                .build();
    }

    private JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            public void beforeJob(JobExecution jobExecution) {
                // Implementation not necessary at this time
            }

            public void afterJob(JobExecution jobExecution) {
                final long start = jobExecution.getCreateTime().getTime();
                final long totalMillis = jobExecution.getEndTime().getTime() - start;
                final long totalSeconds = MILLISECONDS.toSeconds(totalMillis);
                log.info("Job total execution time: {}s", totalSeconds);
            }
        };
    }
}