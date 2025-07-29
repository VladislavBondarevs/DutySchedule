package firstdb.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setText(text, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("duty@ybm.dev");

        System.out.println("Senden der E-Mail an: " + to);
        mailSender.send(mimeMessage);
        System.out.println("E-Mail erfolgreich gesendet an: " + to);
    }
}
