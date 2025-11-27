package nl.fontys.s3.backend.service;

import nl.fontys.s3.backend.dto.LoginRequest;
import nl.fontys.s3.backend.dto.UserRegisterRequest;
import nl.fontys.s3.backend.dto.UserResponse;
import nl.fontys.s3.backend.entity.User;
import nl.fontys.s3.backend.repository.interfaces.UserRepository;
import nl.fontys.s3.backend.service.interfaces.EmailService;
import nl.fontys.s3.backend.service.interfaces.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public UserResponse register(UserRegisterRequest request) {
        // quick check for existing email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already in use"
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(false);
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusDays(1));

        try {
            User saved = userRepository.save(user);
            String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;

            sendVerificationEmailSafely(saved.getEmail(), verificationLink);

            return mapToResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
    }




    @Override
    public UserResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Please verify your email before logging in.");
        }

        return mapToResponse(user);
    }

    @Override
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToResponse(user);
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));

        if (user.getVerificationTokenExpiresAt() != null
                && user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.save(user);
    }


    private UserResponse mapToResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private void sendVerificationEmailSafely(String email, String link) {
        try {
            emailService.sendVerificationEmail(email, link);
        } catch (Exception ex) {
            log.error("Failed to send verification email to {} with link {}", email, link, ex);
        }
    }
}