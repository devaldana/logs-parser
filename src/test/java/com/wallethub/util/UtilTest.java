package com.wallethub.util;

import org.junit.Test;

import java.time.LocalDateTime;

import static com.wallethub.util.Global.DOT_SEPARATOR;
import static com.wallethub.util.Global.SPACE_SEPARATOR;
import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void parseStringToLocalDateTimeDotSeparatedString() {
        final String dotSeparatedDate = "2020-02-29.05:30";
        final LocalDateTime date = LocalDateTime.parse("2020-02-29T05:30");
        final LocalDateTime parsedDate = Util.parseStringToLocalDateTime(dotSeparatedDate, DOT_SEPARATOR);

        assertEquals(date, parsedDate);
    }

    @Test
    public void parseStringToLocalDateTimeSpaceSeparatedString() {
        final String spaceSeparatedDate = "2020-02-29 05:30";
        final LocalDateTime date = LocalDateTime.parse("2020-02-29T05:30");
        final LocalDateTime parsedDate = Util.parseStringToLocalDateTime(spaceSeparatedDate, SPACE_SEPARATOR);

        assertEquals(date, parsedDate);
    }
}