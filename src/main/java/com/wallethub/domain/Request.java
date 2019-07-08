package com.wallethub.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author David Aldana
 * @since 2019.07
 */
@Data
@Entity
@Table(name = "requests")
public class Request extends BaseEntity {

    private LocalDateTime date;
    private String ip;
    private String resource;
    private String status;
    private String userAgent;
}
