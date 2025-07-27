package com.tanishka.ecommerce.serviceimpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl {
	

	    @Autowired
	    private JavaMailSender mailSender;

	    public void sendEmail(String toEmail, String subject, String body) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom(System.getenv("SMTP_USERNAME"));  // or use config class to fetch
	        message.setTo(toEmail);
	        message.setSubject(subject);
	        message.setText(body);

	        mailSender.send(message);
	        System.out.println("Mail sent successfully to " + toEmail);
	    }
	

}
