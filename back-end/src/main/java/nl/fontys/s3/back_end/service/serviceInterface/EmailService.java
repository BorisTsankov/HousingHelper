package nl.fontys.s3.back_end.service.serviceInterface;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
}
