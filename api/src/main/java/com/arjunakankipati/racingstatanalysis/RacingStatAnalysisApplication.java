package com.arjunakankipati.racingstatanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RacingStatAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(RacingStatAnalysisApplication.class, args);
	}

}
