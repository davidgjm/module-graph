package com.davidgjm.oss.maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories
@EntityScan(basePackages = "com.davidgjm.oss.maven.domain")
public class ModuleGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleGraphApplication.class, args);
	}
}
