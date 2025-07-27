package com.tanishka.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	        .info(new Info()
	            .title("Tanishka's Collection API")
	            .version("1.0")
	            .description("API documentation for Tanishka's Collection Spring Boot application"));
	}

}
