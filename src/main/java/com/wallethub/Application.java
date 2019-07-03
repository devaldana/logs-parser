package com.wallethub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import static com.wallethub.util.ArgsValidator.validateArgs;

@SpringBootApplication
public class Application implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String... args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(final ApplicationArguments args) throws Exception {
		logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		validateArgs(args);
	}
}
