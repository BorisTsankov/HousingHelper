package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.LoginRequest;
import nl.fontys.s3.back_end.dto.UserRegisterRequest;
import nl.fontys.s3.back_end.dto.UserResponse;
import nl.fontys.s3.back_end.entity.User;
import nl.fontys.s3.back_end.repository.repositoryInterface.UserRepository;
import nl.fontys.s3.back_end.service.serviceInterface.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private void setId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void register_succeeds_whenEmailNotUsed_andSendsVerificationEmail() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setName("Viktoria");
        request.setPassword("plainPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("hashedPassword");

        User savedUser = new User();
        setId(savedUser, 1L);
        savedUser.setEmail("test@example.com");
        savedUser.setName("Viktoria");
        savedUser.setPassword("hashedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        // Verify user persisted correctly
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User userPassedToRepo = userCaptor.getValue();
        assertThat(userPassedToRepo.getEmail()).isEqualTo("test@example.com");
        assertThat(userPassedToRepo.getName()).isEqualTo("Viktoria");
        assertThat(userPassedToRepo.getPassword()).isEqualTo("hashedPassword");
        assertThat(userPassedToRepo.getVerificationToken()).isNotNull();
        assertThat(userPassedToRepo.isVerified()).isFalse();

        // Verify email is sent
        verify(emailService).sendVerificationEmail(eq("test@example.com"), anyString());

        // Verify response mapping
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Viktoria");
    }

    @Test
    void register_swallowEmailServiceException_andStillReturnsUserResponse() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setName("Viktoria");
        request.setPassword("plainPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("hashedPassword");

        User savedUser = new User();
        setId(savedUser, 1L);
        savedUser.setEmail("test@example.com");
        savedUser.setName("Viktoria");
        savedUser.setPassword("hashedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Email service blows up
        doThrow(new RuntimeException("SMTP down"))
                .when(emailService)
                .sendVerificationEmail(anyString(), anyString());

        UserResponse response = userService.register(request);

        // Service should NOT propagate the exception
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Viktoria");

        verify(emailService).sendVerificationEmail(eq("test@example.com"), anyString());
    }

    @Test
    void register_throwsConflictWhenEmailAlreadyExists_precheck() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setName("Existing User");
        request.setPassword("whatever");

        User existing = new User();
        setId(existing, 99L);
        existing.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email is already in use")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void register_throwsConflictWhenUniqueConstraintViolates() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@example.com");
        request.setName("Viktoria");
        request.setPassword("plainPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email is already in use")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }


    @Test
    void login_succeedsWithCorrectCredentialsAndVerifiedUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainPassword");

        User user = new User();
        setId(user, 1L);
        user.setEmail("test@example.com");
        user.setName("Viktoria");
        user.setPassword("hashedPassword");
        user.setVerified(true);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "hashedPassword"))
                .thenReturn(true);

        UserResponse response = userService.login(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Viktoria");

        verify(passwordEncoder).matches("plainPassword", "hashedPassword");
    }

    @Test
    void login_throwsUnauthorizedWhenEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("pw");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid credentials")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_throwsUnauthorizedWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        setId(user, 1L);
        user.setEmail("test@example.com");
        user.setName("Viktoria");
        user.setPassword("hashedPassword");
        user.setVerified(true);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid credentials")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }

    @Test
    void login_throwsForbiddenWhenUserNotVerified() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainPassword");

        User user = new User();
        setId(user, 1L);
        user.setEmail("test@example.com");
        user.setName("Viktoria");
        user.setPassword("hashedPassword");
        user.setVerified(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "hashedPassword"))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Please verify your email before logging in.")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }


    @Test
    void getByEmail_returnsMappedUserResponseWhenFound() {
        User user = new User();
        setId(user, 1L);
        user.setEmail("test@example.com");
        user.setName("Viktoria");
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        UserResponse response = userService.getByEmail("test@example.com");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Viktoria");
    }

    @Test
    void getByEmail_throwsNotFoundWhenMissing() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByEmail("missing@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    // ---------- verifyEmail ----------

    @Test
    void verifyEmail_succeedsWhenTokenValidAndNotExpired() {
        User user = new User();
        setId(user, 1L);
        user.setVerified(false);
        user.setVerificationToken("token123");
        user.setVerificationTokenExpiresAt(null);

        when(userRepository.findByVerificationToken("token123"))
                .thenReturn(Optional.of(user));

        userService.verifyEmail("token123");

        assertThat(user.isVerified()).isTrue();
        assertThat(user.getVerificationToken()).isNull();
        assertThat(user.getVerificationTokenExpiresAt()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void verifyEmail_throwsBadRequestWhenTokenInvalid() {
        when(userRepository.findByVerificationToken("invalid-token"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.verifyEmail("invalid-token"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid verification token")
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }
}
