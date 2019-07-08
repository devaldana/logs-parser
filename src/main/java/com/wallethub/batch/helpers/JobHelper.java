package com.wallethub.batch.helpers;

import com.wallethub.domain.BlockedIp;
import com.wallethub.util.ArgumentsData;

/**
 * @author David Aldana
 * @since 2019.07
 */
final public class JobHelper {

    public static String getBlockedIpMessage(final BlockedIp blockedIp, final ArgumentsData argumentsData){
        return new StringBuilder()
                .append("Blocked because of exceed the requests threshold [")
                .append(argumentsData.getThreshold())
                .append("] between ")
                .append(argumentsData.getStartDate())
                .append(" and ")
                .append(argumentsData.getEndDate())
                .append(", number of attempts: ")
                .append(blockedIp.getAttempts())
                .toString();
    }

    private JobHelper() {}
}
