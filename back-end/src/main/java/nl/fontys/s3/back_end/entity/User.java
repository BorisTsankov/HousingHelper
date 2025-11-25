package nl.fontys.s3.back_end.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String password; // 🔐 hash later

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expires_at")
    private LocalDateTime verificationTokenExpiresAt;

    public User() {}

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public boolean isVerified() {return verified;}

    public void setVerified(boolean verified) {this.verified = verified;}
    public String getVerificationToken() {return verificationToken;}
    public void setVerificationToken(String verificationToken) {this.verificationToken = verificationToken;}
    public LocalDateTime getVerificationTokenExpiresAt() {return verificationTokenExpiresAt;}
    public void setVerificationTokenExpiresAt(LocalDateTime verificationTokenExpiresAt) {this.verificationTokenExpiresAt = verificationTokenExpiresAt;}
}