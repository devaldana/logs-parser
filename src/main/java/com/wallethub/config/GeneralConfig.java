package com.wallethub.config;

import com.wallethub.enums.Duration;
import com.wallethub.util.ArgumentsData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static com.wallethub.util.Global.ACCESS_LOG_FILE_PATH_ARG;
import static com.wallethub.util.Global.DOT_SEPARATOR;
import static com.wallethub.util.Global.DURATION_ARG;
import static com.wallethub.util.Global.START_DATE_ARG;
import static com.wallethub.util.Global.THRESHOLD_ARG;
import static com.wallethub.util.Util.parseStringToLocalDateTime;


@Configuration
public class GeneralConfig {

    private final Environment env;

    public GeneralConfig(final Environment env) {
        this.env = env;
    }

    @Bean
    public ArgumentsData argumentsData() {
        ArgumentsData argumentsData = new ArgumentsData();
        argumentsData.setAccessLogFilePath(env.getProperty(ACCESS_LOG_FILE_PATH_ARG));
        argumentsData.setStartDate(parseStringToLocalDateTime(env.getProperty(START_DATE_ARG), DOT_SEPARATOR));
        argumentsData.setDuration(Duration.findByName(env.getProperty(DURATION_ARG)).get());
        argumentsData.setThreshold(Integer.valueOf(env.getProperty(THRESHOLD_ARG)));
        return argumentsData;
    }
}
