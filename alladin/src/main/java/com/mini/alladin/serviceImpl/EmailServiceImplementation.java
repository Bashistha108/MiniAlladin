package com.mini.alladin.serviceImpl;

import com.mini.alladin.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImplementation(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void sendUnblockEmail(String to, String unblockLink) {
        try {
            // MimeMessage represents full email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your account has been blocked – Mini-Alladin");
            String htmlContent = "<h2>Hello!</h2>"
                    + "<p>Your account has been blocked.</p>"
                    + "<p>Click the following link  to activate the account again:</p>"
                    + "<a href=\"" + unblockLink + "\">Unblock Account</a>";
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error when sending the email", e);
        }
    }
}
