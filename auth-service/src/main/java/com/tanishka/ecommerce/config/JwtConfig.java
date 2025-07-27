package com.tanishka.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

	@Value("${jwt.secret.key}")
	private String secret;

    public String getSecret() {
        return secret;
    }
}
