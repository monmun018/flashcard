package com.app.flashcard.api.v1;

import com.app.flashcard.api.dto.request.LoginRequest;
import com.app.flashcard.api.dto.request.RegisterRequest;
import com.app.flashcard.api.dto.response.ApiResponse;
import com.app.flashcard.api.dto.response.LoginResponse;
import com.app.flashcard.api.dto.response.UserResponse;
import com.app.flashcard.shared.security.JwtUtil;
import com.app.flashcard.user.model.User;
import com.app.flashcard.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getLoginId());
            String token = jwtUtil.generateToken(userDetails);

            User user = userService.findByUserLoginID(loginRequest.getLoginId());
            UserResponse userResponse = new UserResponse(
                user.getUserID(),
                user.getUserLoginID(),
                user.getUserName(),
                user.getUserAge(),
                user.getUserMail()
            );

            LoginResponse loginResponse = new LoginResponse(token, userResponse);
            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid credentials", "Login failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Authentication failed", e.getMessage()));
        }
    }

    @Operation(summary = "User registration", description = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.existsByUserLoginID(registerRequest.getLoginId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("User already exists", "Login ID is already taken"));
            }

            User user = new User();
            user.setUserLoginID(registerRequest.getLoginId());
            user.setUserPW(passwordEncoder.encode(registerRequest.getPassword()));
            user.setUserName(registerRequest.getName());
            user.setUserAge(registerRequest.getAge());
            user.setUserMail(registerRequest.getEmail());

            User savedUser = userService.save(user);
            
            UserResponse userResponse = new UserResponse(
                savedUser.getUserID(),
                savedUser.getUserLoginID(),
                savedUser.getUserName(),
                savedUser.getUserAge(),
                savedUser.getUserMail()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "User registered successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Registration failed", e.getMessage()));
        }
    }
}