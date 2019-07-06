package com.wallethub.util;

import com.wallethub.enums.Duration;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(builderMethodName = "with")
public class ArgumentsData {
    private String accessLogFilePath;
    private LocalDateTime startDate;
    private Duration duration;
    private int threshold;
}
