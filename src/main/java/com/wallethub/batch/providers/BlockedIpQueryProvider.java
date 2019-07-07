package com.wallethub.batch.providers;

import com.wallethub.domain.BlockedIp;
import com.wallethub.util.ArgumentsData;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.stereotype.Component;

import javax.persistence.Query;

import static com.wallethub.util.Global.BLOCKED_IPS_QUERY;
import static com.wallethub.util.Global.END_DATE_ARG;
import static com.wallethub.util.Global.START_DATE_ARG;
import static com.wallethub.util.Global.THRESHOLD_ARG;

@Component
@AllArgsConstructor
public class BlockedIpQueryProvider extends AbstractJpaQueryProvider {

    private final ArgumentsData argumentsData;

    @Override
    public Query createQuery() {
        final Query query = getEntityManager().createNamedQuery(BLOCKED_IPS_QUERY, BlockedIp.class);
        query.setParameter(START_DATE_ARG, argumentsData.getStartDate());
        query.setParameter(END_DATE_ARG, argumentsData.getEndDate());
        query.setParameter(THRESHOLD_ARG, argumentsData.getThreshold());
        return query;
    }

    @Override
    public void afterPropertiesSet() {
        // Implementation not necessary at this time
    }
}
