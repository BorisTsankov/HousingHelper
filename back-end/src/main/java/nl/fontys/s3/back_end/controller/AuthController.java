package nl.fontys.s3.back_end.controller;

import nl.fontys.s3.back_end.dto.LoginRequest;
import nl.fontys.s3.back_end.dto.UserRegisterRequest;
import nl.fontys.s3.back_end.dto.UserResponse;
import nl.fontys.s3.back_end.security.JwtUtil;
import nl.fontys.s3.back_end.service.serviceInterface.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    public AuthController(UserService userService, JwtUtil jwtUtil) {

        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        UserResponse registered = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        UserResponse user = userService.login(request);
        String token = jwtUtil.generateToken(user.getEmail());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // set true on production HTTPS
                .path("/")
                .sameSite("Lax")
                .maxAge(24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = (String) authentication.getPrincipal();
        UserResponse user = userService.getByEmail(email);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

}