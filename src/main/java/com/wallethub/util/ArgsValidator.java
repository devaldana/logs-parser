package com.wallethub.util;

import com.wallethub.enums.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static com.wallethub.util.Global.ACCESS_LOG_FILE_PATH_ARG;
import static com.wallethub.util.Global.DOT_SEPARATOR;
import static com.wallethub.util.Global.DURATION_ARG;
import static com.wallethub.util.Global.START_DATE_ARG;
import static com.wallethub.util.Global.THRESHOLD_ARG;
import static com.wallethub.util.Util.parseStringToLocalDateTime;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author David Aldana
 * @since 2019.07
 */
@Slf4j
public class ArgsValidator {

    public static void validateArgs(final ApplicationArguments args) {
        printHeader();
        validateArgsNames(args);
        validateArgsValues(args);
        printFooter();
    }

    private static void validateArgsNames(final ApplicationArguments args){
        log.info("Validating args names...");
        final Set<String> argsNames = args.getOptionNames();

        if(!argsNames.contains(START_DATE_ARG) ||
           !argsNames.contains(DURATION_ARG) ||
           !argsNames.contains(THRESHOLD_ARG) ||
           !argsNames.contains(ACCESS_LOG_FILE_PATH_ARG)) {

            throw new IllegalArgumentException("At least one argument is missing.");
        }
    }

    private static void validateArgsValues(final ApplicationArguments args) {
        log.info("Validating args values...");
        final List<String> startDateValues = args.getOptionValues(START_DATE_ARG);
        final List<String> durationValues = args.getOptionValues(DURATION_ARG);
        final List<String> thresholdValues = args.getOptionValues(THRESHOLD_ARG);
        final List<String> accessLogPathValues = args.getOptionValues(ACCESS_LOG_FILE_PATH_ARG);

        validateArgsLists(startDateValues, durationValues, thresholdValues, accessLogPathValues);
        validateStartDate(startDateValues.get(0));
        validateDuration(durationValues.get(0));
        validateThreshold(thresholdValues.get(0));
        validateAccessLogFilePath(accessLogPathValues.get(0));
    }

    private static void validateArgsLists(final List<String> startDateValues,
                                          final List<String> durationValues,
                                          final List<String> thresholdValues,
                                          final List<String> accessLogPathValues) {

        if(invalidArgList(startDateValues) || invalidArgList(durationValues) ||
           invalidArgList(thresholdValues) || invalidArgList(accessLogPathValues)) {
            throw new IllegalArgumentException("Invalid argument: please review the inputs.");
        }
    }

    private static boolean invalidArgList(final List<?> list){
        return isEmpty(list) || list.size() > 1;
    }

    private static void validateStartDate(final String startDate) {
        log.info("Validating startDate argument: {}", startDate);
        try {
            parseStringToLocalDateTime(startDate, DOT_SEPARATOR);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid startDate argument.");
        }
    }

    private static void validateDuration(final String duration) {
        log.info("Validating duration argument: {}", duration);
        Duration.findByName(duration)
                .orElseThrow(() -> new IllegalArgumentException("Invalid duration argument."));
    }

    private static void validateThreshold(final String threshold) {
        log.info("Validating threshold argument: {}", threshold);
        final int thresholdValue;
        try {
            thresholdValue = Integer.parseInt(threshold);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid threshold argument, must be a number.");
        }
        if(thresholdValue <= 0)
            throw new IllegalArgumentException("Threshold must be greater than zero (0).");
    }

    private static void validateAccessLogFilePath(final String accessLogFilePath) {
        log.info("Validating accessLogFilePath argument: {}", accessLogFilePath);
        final Path pathToFile = Paths.get(accessLogFilePath);
        final File accessLog = pathToFile.toFile();
        if(!accessLog.isFile()) throw new IllegalArgumentException("Provide a valid file path.");
        if(!accessLog.canRead()) throw new IllegalStateException("The file can not be read, please check it permissions.");
    }

    private static void printHeader() {
        log.info("ArgsValidator initialized\n\n:. Starting validation of arguments...\n");
    }

    private static void printFooter() {
        log.info("Finishing validations...\n\n.: Arguments successfully validated!\n");
    }
}