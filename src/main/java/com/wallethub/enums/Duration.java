package com.wallethub.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author David Aldana
 * @since 2019.07
 */
public enum Duration {

    HOURLY("hourly"),
    DAILY("daily");

    private String name;

    Duration(String name) {
        this.name = name;
    }

    public static Optional<Duration> findByName(final String name) {
        return Stream.of(values())
                     .filter(duration -> duration.name.equalsIgnoreCase(name))
                     .findFirst();
    }
}
