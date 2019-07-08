package com.wallethub.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.DefaultApplicationArguments;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author David Aldana
 * @since 2019.07
 */
public class ArgsValidatorTest {

    private static final String ACCESS_LOG_TEST_PATH = ".." + File.separator + "qwqEhvLchSamETmXwp.log";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // qwqEhvLchSamETmXwp.log file is guaranteed to exist,
        // it is temporary created if it doesn't exists before each test.
        final Path filePath = Paths.get(ACCESS_LOG_TEST_PATH);
        if(!Files.exists(filePath)) Files.createFile(filePath);
    }

    @After
    public void cleanUp() throws Exception {
        // qwqEhvLchSamETmXwp.log is deleted after each test.
        Files.deleteIfExists(Paths.get(ACCESS_LOG_TEST_PATH));
    }

    @Test
    public void validateArgsHappyPathTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=200",
                                "--accesslog=" + ACCESS_LOG_TEST_PATH };

        // Should NOT throw any exception.
        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void invalidStartDateTest() {
        final String[] args = { "--startDate=2017-01-01 15:00:00",
                                "--duration=hourly",
                                "--threshold=200",
                                "--accesslog=" + ACCESS_LOG_TEST_PATH };

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid startDate argument.");

        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void invalidDurationTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=monthly",
                                "--threshold=200",
                                "--accesslog=" + ACCESS_LOG_TEST_PATH };

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid duration argument.");

        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void invalidNumberThresholdTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=500L",
                                "--accesslog=" + ACCESS_LOG_TEST_PATH };

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid threshold argument, must be a number.");

        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void invalidNegativeThresholdTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=-200",
                                "--accesslog=" + ACCESS_LOG_TEST_PATH };

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Threshold must be greater than zero (0).");

        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void invalidZeroThresholdTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=0",
                                "--accesslog=" + ACCESS_LOG_TEST_PATH };

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Threshold must be greater than zero (0).");

        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void invalidLogFilePathTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=200",
                                "--accesslog=../acc*ess.log" };

        thrown.expect(IllegalArgumentException.class);
        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void missingArgumentsTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=200" };

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("At least one argument is missing.");
        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void missingArgumentValueTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=200",
                                "--accesslog" };

        // accesslog argument exists but doesn't have any value
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid argument: please review the inputs.");
        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }

    @Test
    public void moreThanOneValueArgumentTest() {
        final String[] args = { "--startDate=2017-01-01.15:00:00",
                                "--duration=hourly",
                                "--threshold=200",
                                "--accesslog=../wRvwPWkd.log",
                                "--accesslog=../UiqOPxIe.log" };

        // accesslog exist but have 2 values: ../wRvwPWkd.log and ../UiqOPxIe.log
        // and just one value is accepted
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid argument: please review the inputs.");
        ArgsValidator.validateArgs(new DefaultApplicationArguments(args));
    }
}