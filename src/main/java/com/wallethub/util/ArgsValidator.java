package com.wallethub.util;

import com.wallethub.enums.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.Set;

import static com.wallethub.util.Global.DOT_SEPARATOR;
import static com.wallethub.util.Global.DURATION_ARG;
import static com.wallethub.util.Global.START_DATE_ARG;
import static com.wallethub.util.Global.THRESHOLD_ARG;
import static com.wallethub.util.Util.parseStringToLocalDateTime;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ArgsValidator {

    private static final Logger logger = LoggerFactory.getLogger(ArgsValidator.class);

    public static void validateArgs(final ApplicationArguments args) {
        validateArgsNames(args);
        validateArgsValues(args);
    }

    private static void validateArgsNames(final ApplicationArguments args){
        logger.info("Validating args names");
        final Set<String> argsNames = args.getOptionNames();

        if(!argsNames.contains(START_DATE_ARG) ||
           !argsNames.contains(DURATION_ARG) ||
           !argsNames.contains(THRESHOLD_ARG)) {

            throw new IllegalArgumentException("At least one argument is missing");
        }
    }

    private static void validateArgsValues(final ApplicationArguments args) {
        logger.info("Validating args values");
        final List<String> startDateValues = args.getOptionValues(START_DATE_ARG);
        final List<String> durationValues = args.getOptionValues(DURATION_ARG);
        final List<String> thresholdValues = args.getOptionValues(THRESHOLD_ARG);

        validateArgsLists(startDateValues, durationValues, thresholdValues);
        validateStartDate(startDateValues.get(0));
        validateDuration(durationValues.get(0));
        validateThreshold(thresholdValues.get(0));
    }

    private static void validateArgsLists(final List<String> startDateValues,
                                          final List<String> durationValues,
                                          final List<String> thresholdValues) {

        if(invalidArgList(startDateValues) || invalidArgList(durationValues) || invalidArgList(thresholdValues)) {
            throw new IllegalArgumentException("Invalid argument: please review the inputs");
        }
    }

    private static boolean invalidArgList(final List<?> list){
        return isEmpty(list) || list.size() > 1;
    }

    private static void validateStartDate(final String startDate) {
        logger.info("Validating startDate argument: {}", startDate);
        try {
            parseStringToLocalDateTime(startDate, DOT_SEPARATOR);
        } catch (Exception exception) {
            logger.error("Error parsing startDate");
            throw new IllegalArgumentException("Invalid startDate argument");
        }
    }

    private static void validateDuration(final String duration) {
        logger.info("Validating duration argument: {}", duration);
        Duration.findByName(duration)
                .orElseThrow(() -> new IllegalArgumentException("Invalid duration argument"));
    }

    private static void validateThreshold(final String threshold) {
        logger.info("Validating threshold argument: {}", threshold);
        try {
            Integer.parseInt(threshold);
        } catch (Exception exception) {
            logger.error("Error parsing threshold");
            throw new IllegalArgumentException("Invalid threshold argument");
        }
    }
}