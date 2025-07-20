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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Dein Konto wurde blockiert – Mini-Alladin");
            String htmlContent = "<h2>Hallo!</h2>"
                    + "<p>Dein Konto wurde blockiert.</p>"
                    + "<p>Klicke auf folgenden Link, um es wieder zu aktivieren:</p>"
                    + "<a href=\"" + unblockLink + "\">Konto entsperren</a>";
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Fehler beim Senden der E-Mail", e);
        }
    }
}
