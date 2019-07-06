package com.wallethub.batch.providers;

import com.wallethub.domain.Request;
import com.wallethub.util.ArgumentsData;
import lombok.Setter;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.stereotype.Component;

import javax.persistence.Query;

@Setter
@Component
public class RequestQueryProvider extends AbstractJpaQueryProvider {

    private final ArgumentsData argumentsData;

    public RequestQueryProvider(final ArgumentsData argumentsData) {
        this.argumentsData = argumentsData;
    }

    @Override
    public Query createQuery() {
        System.out.println(argumentsData.getStartDate());
        System.out.println(argumentsData.getEndDate());
        Query namedQuery = getEntityManager().createNamedQuery("findRequestByDate", Request.class);
        namedQuery.setParameter("date", argumentsData.getStartDate());
        return namedQuery;
    }

    @Override
    public void afterPropertiesSet() {
        // Implementation not necessary at this time
    }
}
