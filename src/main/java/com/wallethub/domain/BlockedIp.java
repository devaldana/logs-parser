package com.wallethub.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import static com.wallethub.util.Global.BLOCKED_IPS_QUERY;

@Data
@Entity
@Table(name = "ips_blacklist")
@NamedNativeQuery(
        name=BLOCKED_IPS_QUERY,
        query = "SELECT id, '' AS message, ip, COUNT(ip) AS attempts " +
                "FROM requests " +
                "WHERE date BETWEEN :startDate AND :endDate " +
                "GROUP BY ip " +
                "HAVING COUNT(ip) > :threshold",
        resultClass = BlockedIp.class)
public class BlockedIp extends BaseEntity {

    private String ip;
    private int attempts;
    private String message;
}
