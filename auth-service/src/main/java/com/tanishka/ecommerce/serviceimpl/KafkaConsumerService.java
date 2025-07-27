package com.tanishka.ecommerce.serviceimpl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.event.ResetPasswordEvent;
import com.tanishka.ecommerce.event.UserCreatedEvent;

@Service
public class KafkaConsumerService {

	@KafkaListener(topics = "${kafka.topic.user.created}", groupId = "user-events-group")
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Received UserCreatedEvent: " + event);
    }

    @KafkaListener(topics = "${kafka.topic.reset.password}", groupId = "reset-password-group")
    public void handleResetPassword(ResetPasswordEvent event) {
        System.out.println("Received ResetPasswordEvent: " + event);
    }

}
