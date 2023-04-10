package com.example.sqsDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SqsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqsDemoApplication.class, args);
	}

}
