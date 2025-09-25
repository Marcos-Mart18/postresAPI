package com.postres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.postres.repository")
@EntityScan(basePackages = "com.postres.entity")
public class PostresApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostresApiApplication.class, args);
	}

}
