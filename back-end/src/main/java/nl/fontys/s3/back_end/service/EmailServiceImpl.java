package nl.fontys.s3.back_end.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import nl.fontys.s3.back_end.service.serviceInterface.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

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
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }
}