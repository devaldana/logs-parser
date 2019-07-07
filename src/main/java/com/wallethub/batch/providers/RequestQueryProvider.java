package com.wallethub.batch.providers;

import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import lombok.Setter;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.stereotype.Component;

import javax.persistence.Query;

import java.time.LocalDateTime;

import static com.wallethub.util.Global.END_DATE_ARG;
import static com.wallethub.util.Global.START_DATE_ARG;

@Setter
@Component
public class RequestQueryProvider extends AbstractJpaQueryProvider {

    private final ArgumentsData argumentsData;

    public RequestQueryProvider(final ArgumentsData argumentsData) {
        this.argumentsData = argumentsData;
    }

    @Override
    public Query createQuery() {
        Query namedQuery = getEntityManager().createNamedQuery(Request.REQUESTS_IN_DATE_RANGE_QUERY, Request.class);
        namedQuery.setParameter(START_DATE_ARG, argumentsData.getStartDate());
        namedQuery.setParameter(END_DATE_ARG, argumentsData.getEndDate());
        return namedQuery;
    }

    @Override
    public void afterPropertiesSet() {
        // Implementation not necessary at this time
    }
}
