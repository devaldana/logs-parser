package com.wallethub.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.time.LocalDateTime;

@Data
@Entity
@NamedQuery(name=Request.REQUESTS_IN_DATE_RANGE_QUERY, query = "SELECT req FROM Request req WHERE req.date BETWEEN :startDate AND :endDate ORDER BY req.date")
public class Request {

    public static final String REQUESTS_IN_DATE_RANGE_QUERY = "findRequestsInDateRange";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private String ip;
    private String resource;
    private String status;
    private String userAgent;
}
