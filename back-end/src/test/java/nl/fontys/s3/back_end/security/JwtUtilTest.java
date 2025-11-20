package nl.fontys.s3.back_end.security;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        jwtUtil = new JwtUtil();
    }


    @Test
    void generateToken_containsCorrectEmail() {
        String email = "test@example.com";

        String token = jwtUtil.generateToken(email);
        assertThat(token).isNotBlank();

        // extract back
        String subject = jwtUtil.extractEmail(token);
        assertThat(subject).isEqualTo(email);
    }


    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtUtil.generateToken("viktoria@example.com");

        boolean valid = jwtUtil.isTokenValid(token);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForTamperedToken() {
        String token = jwtUtil.generateToken("foo@example.com");

        // Tamper token by removing a char
        String brokenToken = token.substring(0, token.length() - 2) + "xx";

        boolean valid = jwtUtil.isTokenValid(brokenToken);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("expired@example.com")
                .setIssuedAt(new java.util.Date(System.currentTimeMillis() - 10000))
                .setExpiration(new java.util.Date(System.currentTimeMillis() - 5000)) // already expired
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                "super-secret-jwt-key-change-this-1234567890".getBytes()
                        ),
                        SignatureAlgorithm.HS256
                )
                .compact();

        boolean valid = jwtUtil.isTokenValid(expiredToken);

        assertThat(valid).isFalse();
    }


    @Test
    void extractEmail_returnsCorrectSubject() {
        String token = jwtUtil.generateToken("boris@example.com");

        String email = jwtUtil.extractEmail(token);

        assertThat(email).isEqualTo("boris@example.com");
    }
}
