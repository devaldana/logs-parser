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
@NamedQuery(name="findRequestByDate", query = "SELECT r FROM Request r WHERE r.date = :date")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private String ip;
    private String resource;
    private String status;
    private String userAgent;
}
