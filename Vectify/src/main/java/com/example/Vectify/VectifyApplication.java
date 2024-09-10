package com.example.Vectify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class VectifyApplication {
	public static void main(String[] args) {
		SpringApplication.run(VectifyApplication.class, args);
	}
}
