package com.wallethub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import static com.wallethub.util.ArgsValidator.validateArgs;

/**
 * @author David Aldana
 * @since 2019.07
 */
@Slf4j
@SpringBootApplication
public class Application {

	public static void main(String... args) {
		log.info("Application started with command-line arguments: {}", Arrays.toString(args));
		validateArgs(new DefaultApplicationArguments(args));
		SpringApplication.run(Application.class, args);
	}
}
