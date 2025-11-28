package nl.fontys.s3.backend.service.interfaces;

import nl.fontys.s3.backend.dto.LoginRequest;
import nl.fontys.s3.backend.dto.UserRegisterRequest;
import nl.fontys.s3.backend.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    UserResponse login(LoginRequest request);
    UserResponse getByEmail(String email);
    void verifyEmail(String token);

}