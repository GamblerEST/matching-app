package com.matchme.match_me.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchme.match_me.auth.dto.AuthResponse;
import com.matchme.match_me.auth.dto.LoginRequest;
import com.matchme.match_me.auth.dto.RegisterRequest;
import com.matchme.match_me.users.User;
import com.matchme.match_me.users.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          JwtService jwtService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless JWT: client must discard token; server simply acknowledges
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long userId;
        if (principal instanceof Long id) {
            userId = id;
        } else if (principal instanceof String s) {
            userId = Long.valueOf(s);
        } else {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getId());

        return ResponseEntity.ok(new AuthResponse(user.getId(), token));
    }
}
