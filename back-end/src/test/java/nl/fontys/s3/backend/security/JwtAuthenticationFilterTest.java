package nl.fontys.s3.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.fontys.s3.backend.entity.User;
import nl.fontys.s3.backend.repository.interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        // Default path: protected endpoint, so filter normally runs JWT logic
        when(request.getServletPath()).thenReturn("/api/some-protected-resource");
    }

    void doFilterInternal_skipsAuthenticationForAuthEndpoints() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/auth/login");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtil, never()).isTokenValid(anyString());
        verify(jwtUtil, never()).extractEmail(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_setsAuthenticationWhenJwtIsValid() throws ServletException, IOException {
        String jwt = "valid.jwt.token";

        Cookie[] cookies = {new Cookie("jwt", jwt)};
        when(request.getCookies()).thenReturn(cookies);

        when(jwtUtil.isTokenValid(jwt)).thenReturn(true);
        when(jwtUtil.extractEmail(jwt)).thenReturn("test@example.com");

        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Run filter
        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo("test@example.com");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_noCookies_doesNotAuthenticate() throws Exception {
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidJwt_doesNotAuthenticate() throws Exception {
        Cookie[] cookies = {new Cookie("jwt", "broken-token")};
        when(request.getCookies()).thenReturn(cookies);

        when(jwtUtil.isTokenValid("broken-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_userNotFound_doesNotAuthenticate() throws Exception {
        String jwt = "valid.token";

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("jwt", jwt)});
        when(jwtUtil.isTokenValid(jwt)).thenReturn(true);
        when(jwtUtil.extractEmail(jwt)).thenReturn("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_authAlreadyPresent_skipsAuthenticationSetup() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "existingUser", null, java.util.Collections.emptyList()
                )
        );

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("jwt", "whatever")});
        when(jwtUtil.isTokenValid("whatever")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo("existingUser");

        verify(jwtUtil, times(1)).isTokenValid("whatever");
        verify(jwtUtil, never()).extractEmail(any());
        verify(userRepository, never()).findByEmail(anyString());

        verify(filterChain).doFilter(request, response);
    }
}
