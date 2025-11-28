package nl.fontys.s3.backend.service;

import nl.fontys.s3.backend.service.interfaces.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verify your email");
            String content = "<p>Hi,</p>" +
                    "<p>Thank you for registering. Please click the link below to verify your email:</p>" +
                    "<p><a href=\"" + verificationLink + "\">Verify my account</a></p>" +
                    "<p>If you did not create this account, you can ignore this email.</p>";

            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send verification email to {} with link {}", to, verificationLink, e);
        }
    }
}
