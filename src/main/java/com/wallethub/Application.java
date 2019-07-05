package com.wallethub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import static com.wallethub.util.ArgsValidator.validateArgs;

@Slf4j
@SpringBootApplication
public class Application implements ApplicationRunner {

	public static void main(String... args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(final ApplicationArguments args) throws Exception {
		log.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
		validateArgs(args);
	}
}
