package com.wallethub.batch.writers;

import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestWriter implements ItemWriter<Request> {

    private final ArgumentsData argumentsData;
    private final JpaItemWriter<Request> requestJpaItemWriter;

    @Autowired
    public RequestWriter(@Qualifier("requestJpaItemWriter") final JpaItemWriter<Request> requestJpaItemWriter,
                         final ArgumentsData argumentsData) {
        this.requestJpaItemWriter = requestJpaItemWriter;
        this.argumentsData = argumentsData;
    }

    @Override
    public void write(List<? extends Request> list) throws Exception {
        requestJpaItemWriter.write(list);
        list.stream().filter( r -> r.getDate().equals(argumentsData.getStartDate())).forEach(System.out::println);
    }
}
