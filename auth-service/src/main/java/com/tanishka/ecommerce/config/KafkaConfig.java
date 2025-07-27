package com.tanishka.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {


	 @Bean
	    public NewTopic userCreatedTopic() {
	        return TopicBuilder.name("user.created")
	                .partitions(1)
	                .replicas(1)
	                .build();
	    }

	    @Bean
	    public NewTopic resetPasswordTopic() {
	        return TopicBuilder.name("reset.password")
	                .partitions(1)
	                .replicas(1)
	                .build();
	    }
	

}
