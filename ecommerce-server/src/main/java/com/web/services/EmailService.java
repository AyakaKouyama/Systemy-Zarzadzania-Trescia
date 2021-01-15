package com.web.services;

import com.ecommerce.data.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final String email;

    public EmailService(JavaMailSender emailSender, @Value("${contact.email}") String email) {
        this.emailSender = emailSender;
        this.email = email;
    }

    public void sendMessage(String to, String messageToSend) throws EmailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo(email);
        message.setSubject("[SZT Ecommerce] Nowa wiadomość od " + to);
        message.setText(messageToSend);

        try {
            emailSender.send(message);
        } catch (Exception e) {
            throw new EmailException("An error occured during sending an email");
        }
    }
}
