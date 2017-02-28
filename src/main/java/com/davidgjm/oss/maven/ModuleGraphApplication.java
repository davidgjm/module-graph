package com.davidgjm.oss.maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationConfig.class)
public class ModuleGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleGraphApplication.class, args);
	}
}
