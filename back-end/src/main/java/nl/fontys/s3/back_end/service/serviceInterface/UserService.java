package nl.fontys.s3.back_end.service.serviceInterface;

import nl.fontys.s3.back_end.dto.LoginRequest;
import nl.fontys.s3.back_end.dto.UserRegisterRequest;
import nl.fontys.s3.back_end.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    UserResponse login(LoginRequest request);
    UserResponse getByEmail(String email);
    void verifyEmail(String token);

}