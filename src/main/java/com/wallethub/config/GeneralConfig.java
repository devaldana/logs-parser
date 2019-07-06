package com.wallethub.config;

import com.wallethub.domain.Request;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class GeneralConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean("requestJpaItemWriter")
    public JpaItemWriter<Request> requestJpaItemWriter() {
        final JpaItemWriter<Request> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
