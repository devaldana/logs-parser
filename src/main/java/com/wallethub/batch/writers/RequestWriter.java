package com.wallethub.batch.writers;

import com.wallethub.domain.Request;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestWriter implements ItemWriter<Request> {

    @Value("${startDate}")
    private String startDateArg;
    private final JpaItemWriter<Request> requestJpaItemWriter;

    @Autowired
    public RequestWriter(@Qualifier("requestJpaItemWriter") JpaItemWriter<Request> requestJpaItemWriter) {
        this.requestJpaItemWriter = requestJpaItemWriter;
    }

    @Override
    public void write(List<? extends Request> list) throws Exception {
        requestJpaItemWriter.write(list);
    }
}
