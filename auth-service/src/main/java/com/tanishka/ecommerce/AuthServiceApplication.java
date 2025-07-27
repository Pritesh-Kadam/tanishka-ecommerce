package com.tanishka.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@EntityScan(basePackages = "com.tanishka.ecommerce.entity")
@EnableJpaRepositories(basePackages = "com.tanishka.ecommerce.repository")
@EnableTransactionManagement
@SpringBootApplication

public class AuthServiceApplication {

	public static void main(String[] args) {
		
		 Dotenv dotenv = Dotenv.configure()
		            .directory(".")
		            .ignoreIfMalformed()
		            .ignoreIfMissing()
		            .load();

		        // Optionally, make sure they're set as system properties
		        dotenv.entries().forEach(entry ->
		            System.setProperty(entry.getKey(), entry.getValue())
		        );

				SpringApplication.run(AuthServiceApplication.class, args);
				
				
		    }

	

}
