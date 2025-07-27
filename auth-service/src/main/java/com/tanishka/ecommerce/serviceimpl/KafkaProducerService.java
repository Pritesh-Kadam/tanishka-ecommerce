package com.tanishka.ecommerce.serviceimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.event.ResetPasswordEvent;
import com.tanishka.ecommerce.event.UserCreatedEvent;
@Service
public class KafkaProducerService {

	    private final KafkaTemplate<String, UserCreatedEvent> userCreatedKafkaTemplate;
	    private final KafkaTemplate<String, ResetPasswordEvent> resetPasswordKafkaTemplate;

	    @Value("${kafka.topic.user.created}")
	    private String userCreatedTopic;

	    @Value("${kafka.topic.reset.password}")
	    private String resetPasswordTopic;

	    public KafkaProducerService(KafkaTemplate<String, UserCreatedEvent> userCreatedKafkaTemplate,
	                                KafkaTemplate<String, ResetPasswordEvent> resetPasswordKafkaTemplate) {
	        this.userCreatedKafkaTemplate = userCreatedKafkaTemplate;
	        this.resetPasswordKafkaTemplate = resetPasswordKafkaTemplate;
	    }

	    public void sendUserCreatedEvent(UserCreatedEvent event) {
	        userCreatedKafkaTemplate.send(userCreatedTopic, event.getEmail(), event);
	        System.out.println("UserCreatedEvent sent: " + event);

	    }

	    public void sendResetPasswordEvent(ResetPasswordEvent event) {
	        resetPasswordKafkaTemplate.send(resetPasswordTopic, event.getEmail(), event);
	        System.out.println("ResetPasswordEvent sent: " + event);

	    }
	

}
