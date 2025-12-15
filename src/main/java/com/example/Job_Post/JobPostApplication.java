package com.example.Job_Post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class JobPostApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPostApplication.class, args);
	}

	@GetMapping("/hello")
	public String helloWorld() {
		return "Hello, World!";
	}
	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to the Job Post API!";
	}

	

}
