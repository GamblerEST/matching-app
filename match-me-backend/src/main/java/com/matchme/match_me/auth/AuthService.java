package com.matchme.match_me.auth;

import com.matchme.match_me.auth.dto.AuthResponse;
import com.matchme.match_me.auth.dto.LoginRequest;
import com.matchme.match_me.auth.dto.RegisterRequest;
import com.matchme.match_me.bio.Bio;
import com.matchme.match_me.bio.BioRepository;
import com.matchme.match_me.common.exception.BadRequestException;
import com.matchme.match_me.common.exception.ConflictException;
import com.matchme.match_me.common.exception.UnauthorizedException;
import com.matchme.match_me.location.Location;
import com.matchme.match_me.location.LocationRepository;
import com.matchme.match_me.profile.Profile;
import com.matchme.match_me.profile.ProfileRepository;
import com.matchme.match_me.users.User;
import com.matchme.match_me.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final BioRepository bioRepository;
    private final ProfileRepository profileRepository;
    private final LocationRepository locationRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService, BioRepository bioRepository,
            ProfileRepository profileRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.bioRepository = bioRepository;
        this.profileRepository = profileRepository;
        this.locationRepository = locationRepository;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email is already registered");
        }

        String username = resolveUsername(request.username(), request.email());

        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new ConflictException("Username is already taken");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setUsername(username);

        userRepository.save(user);

        Bio bio = new Bio();
        bio.setUser(user);
        bioRepository.save(bio);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setComplete(false);
        profileRepository.save(profile);

        Location location = new Location();
        location.setUser(user);
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        location.setMaxRadiusKm(0.0);
        location.setCity("");
        locationRepository.save(location);

        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(user.getId(), token);
    }

    public AuthResponse login(LoginRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId());
        return new AuthResponse(user.getId(), token);
    }

    private String resolveUsername(String requestedUsername, String email) {
        String base = (requestedUsername != null && !requestedUsername.isBlank())
                ? requestedUsername.trim()
                : email.split("@")[0];

        if (base.isBlank()) {
            throw new BadRequestException("Username cannot be empty");
        }

        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsernameIgnoreCase(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }
}