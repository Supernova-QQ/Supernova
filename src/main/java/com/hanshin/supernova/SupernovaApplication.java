package com.hanshin.supernova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SupernovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupernovaApplication.class, args);
	}

}
