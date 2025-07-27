package com.tanishka.ecommerce.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.event.UserCreatedEvent;

@Service
public class UserEventProducer {

	@Autowired
	private KafkaTemplate<String, UserCreatedEvent> kafkaTemplatae;
	
	public void sendUserCreatedEvent(UserCreatedEvent event) {
		kafkaTemplatae.send("user.created", event);
        System.out.println("Sent message: " + event);

	}
}
