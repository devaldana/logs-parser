package com.wallethub.util;

import java.time.LocalDateTime;

final public class Util {

    public static LocalDateTime parseStringToLocalDateTime(final String dateTime, final String separator) {
        return LocalDateTime.parse(dateTime.replaceFirst(separator, "T"));
    }

    private Util() {}
}
