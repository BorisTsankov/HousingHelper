package nl.fontys.s3.back_end.controller;

import nl.fontys.s3.back_end.entity.User;
import nl.fontys.s3.back_end.dto.UserRegistrationDto;
import nl.fontys.s3.back_end.repository.repositoryInterface.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    public UserController(UserRepository repo) { this.repo = repo; }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto dto) {
        if (repo.findByEmail(dto.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User user = new User(dto.email(), dto.name(), dto.password());
        return ResponseEntity.ok(repo.save(user));
    }
}