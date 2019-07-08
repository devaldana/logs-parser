package com.wallethub.util;

import java.time.LocalDateTime;

import static com.wallethub.util.Global.ISO_TIME_VALUE_INDICATOR;

/**
 * @author David Aldana
 * @since 2018.07
 */
final public class Util {

    public static LocalDateTime parseStringToLocalDateTime(final String dateTime, final String timeSeparator) {
        return LocalDateTime.parse(dateTime.replaceFirst(timeSeparator, ISO_TIME_VALUE_INDICATOR));
    }

    private Util() {}
}
