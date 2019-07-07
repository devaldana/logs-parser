package com.wallethub.batch.providers;

import com.wallethub.domain.BlockedIp;
import com.wallethub.util.ArgumentsData;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.stereotype.Component;

import javax.persistence.Query;

import static com.wallethub.util.Global.BLOCKED_IPS_QUERY;
import static com.wallethub.util.Global.END_DATE_ARG;
import static com.wallethub.util.Global.START_DATE_ARG;
import static com.wallethub.util.Global.THRESHOLD_ARG;

@Component
public class RequestQueryProvider extends AbstractJpaQueryProvider {

    private final ArgumentsData argumentsData;

    public RequestQueryProvider(final ArgumentsData argumentsData) {
        this.argumentsData = argumentsData;
    }

    @Override
    public Query createQuery() {
        Query namedQuery = getEntityManager().createNamedQuery(BLOCKED_IPS_QUERY, BlockedIp.class);
        namedQuery.setParameter(START_DATE_ARG, argumentsData.getStartDate());
        namedQuery.setParameter(END_DATE_ARG, argumentsData.getEndDate());
        namedQuery.setParameter(THRESHOLD_ARG, argumentsData.getThreshold());
        return namedQuery;
    }

    @Override
    public void afterPropertiesSet() {
        // Implementation not necessary at this time
    }
}
