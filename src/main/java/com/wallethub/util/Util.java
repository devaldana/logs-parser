package com.wallethub.util;

import java.time.LocalDateTime;

import static com.wallethub.util.Global.ISO_TIME_VALUE_INDICATOR;
import static java.util.Objects.isNull;

final public class Util {

    public static LocalDateTime parseStringToLocalDateTime(final String dateTime, final String timeSeparator) {
        return LocalDateTime.parse(dateTime.replaceFirst(timeSeparator, ISO_TIME_VALUE_INDICATOR));
    }

    public static boolean isBetween(final LocalDateTime startDate,
                                    final LocalDateTime endDate,
                                    final LocalDateTime dateToEvaluate){

        validateArgs(startDate, endDate, dateToEvaluate);

        // Evaluate if a date is between other two dates inclusive
        if(dateToEvaluate.equals(startDate) || dateToEvaluate.equals(endDate)) return true;
        if(dateToEvaluate.isAfter(startDate) && dateToEvaluate.isBefore(endDate)) return true;
        return false;
    }

    private static void validateArgs(final LocalDateTime startDate,
                                     final LocalDateTime endDate,
                                     final LocalDateTime dateToEvaluate) {

        if(isNull(startDate) || isNull(endDate) || isNull(dateToEvaluate))
            throw new IllegalArgumentException("At least one date is invalid (null).");
    }

    private Util() {}
}
