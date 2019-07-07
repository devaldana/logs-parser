package com.wallethub.util;

import org.junit.Test;

import java.time.LocalDateTime;

import static com.wallethub.enums.Duration.DAILY;
import static com.wallethub.enums.Duration.HOURLY;
import static org.junit.Assert.assertEquals;

public class ArgumentsDataTest {

    @Test
    public void getEndDateHourly() {
        final LocalDateTime startDate = LocalDateTime.parse("2020-02-28T05:30");
        final LocalDateTime endDate = LocalDateTime.parse("2020-02-28T06:30");
        final ArgumentsData argumentsData = ArgumentsData.with().startDate(startDate).duration(HOURLY).build();

        assertEquals(endDate, argumentsData.getEndDate());
    }

    @Test
    public void getEndDateDaily() {
        final LocalDateTime startDate = LocalDateTime.parse("2020-02-28T05:30");
        final LocalDateTime endDate = LocalDateTime.parse("2020-02-29T05:30");
        final ArgumentsData argumentsData = ArgumentsData.with().startDate(startDate).duration(DAILY).build();

        assertEquals(endDate, argumentsData.getEndDate());
    }
}