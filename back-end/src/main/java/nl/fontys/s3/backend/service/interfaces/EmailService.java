package nl.fontys.s3.backend.service.interfaces;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
}
