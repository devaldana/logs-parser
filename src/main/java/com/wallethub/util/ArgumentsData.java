package com.wallethub.util;

import com.wallethub.enums.Duration;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArgumentsData {
    private String accessLogFilePath;
    private LocalDateTime startDate;
    private Duration duration;
    private int threshold;
}
