package nl.fontys.s3.backend.controller;

import nl.fontys.s3.backend.dto.LoginRequest;
import nl.fontys.s3.backend.dto.UserRegisterRequest;
import nl.fontys.s3.backend.dto.UserResponse;
import nl.fontys.s3.backend.repository.interfaces.UserRepository;
import nl.fontys.s3.backend.security.JwtUtil;
import nl.fontys.s3.backend.service.interfaces.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void register_returnsCreatedUser() throws Exception {

        UserRegisterRequest req = new UserRegisterRequest("John", "john@example.com", "pwd123");

        UserResponse response = new UserResponse(
                1L,
                "john@example.com",
                "John",
                OffsetDateTime.parse("2018-12-12T13:30:30+05:00")

        );

        when(userService.register(any())).thenReturn(response);

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"John",
                                    "email":"john@example.com",
                                    "password":"pwd123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }


    @Test
    void login_returnsUserAndCookie() throws Exception {

        LoginRequest request = new LoginRequest("john@example.com", "pwd123");

        UserResponse user = new UserResponse(
                1L,
                "john@example.com",
                "John",
                OffsetDateTime.parse("2018-12-12T13:30:30+05:00")
        );

        when(userService.login(any())).thenReturn(user);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("fake-jwt-token");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email":"john@example.com",
                                    "password":"pwd123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("jwt=fake-jwt-token")))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }


    @Test
    void me_returnsUser_ifAuthenticated() throws Exception {

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("john@example.com");

        UserResponse user = new UserResponse(
                1L,
                "john@example.com",
                "John",
                OffsetDateTime.parse("2018-12-12T13:30:30+05:00")
        );

        when(userService.getByEmail("john@example.com")).thenReturn(user);

        mvc.perform(get("/api/auth/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }


    @Test
    void me_returnsUnauthorized_whenNotAuthenticated() throws Exception {

        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void logout_clearsCookie() throws Exception {

        mvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie",
                        org.hamcrest.Matchers.containsString("jwt=")))
                .andExpect(header().string("Set-Cookie",
                        org.hamcrest.Matchers.containsString("Max-Age=0")));
    }
}
