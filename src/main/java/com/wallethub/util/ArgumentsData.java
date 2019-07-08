package com.wallethub.util;

import com.wallethub.enums.Duration;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static com.wallethub.enums.Duration.DAILY;
import static com.wallethub.enums.Duration.HOURLY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author David Aldana
 * @since 2019.07
 */
@Data
@Builder(builderMethodName = "with")
public class ArgumentsData {

    private String accessLogFilePath;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Duration duration;
    private int threshold;

    public LocalDateTime getEndDate() {
        if(isNull(endDate) && nonNull(startDate)) {
            if(DAILY.equals(duration)) {
                endDate = startDate.plusDays(1);
            } else if(HOURLY.equals(duration)) {
                endDate = startDate.plusHours(1);
            }
        }
        return endDate;
    }
}
