package nl.fontys.s3.back_end.controller;

import nl.fontys.s3.back_end.model.User;
import nl.fontys.s3.back_end.dto.UserRegistrationDto;
import nl.fontys.s3.back_end.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    public UserController(UserRepository repo) { this.repo = repo; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
        if (repo.findByEmail(dto.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        User user = new User(dto.email(), dto.name(), dto.password());
        return ResponseEntity.ok(repo.save(user));
    }
}