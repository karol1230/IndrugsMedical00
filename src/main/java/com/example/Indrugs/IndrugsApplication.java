package com.example.Indrugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling

@SpringBootApplication
public class  IndrugsApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndrugsApplication.class, args);
	}

}

