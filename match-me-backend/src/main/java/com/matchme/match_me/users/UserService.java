package com.matchme.match_me.users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.matchme.match_me.common.exception.BadRequestException;
import com.matchme.match_me.common.exception.ConflictException;
import com.matchme.match_me.common.exception.NotFoundException;
import com.matchme.match_me.profile.Profile;
import com.matchme.match_me.profile.ProfileRepository;
import com.matchme.match_me.users.dto.CreateUserRequest;
import com.matchme.match_me.users.dto.UpdateUserRequest;
import com.matchme.match_me.users.dto.UserResponse;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                      ProfileRepository profileRepository,
                      BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        // Always persist a unique username (column is non-null + unique)
        String baseUsername = request.email().split("@")[0];
        if (baseUsername.isBlank()) {
            throw new BadRequestException("Username cannot be empty");
        }
        String candidate = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsernameIgnoreCase(candidate)) {
            candidate = baseUsername + suffix;
            suffix++;
        }
        user.setUsername(candidate);

        userRepository.save(user);

        return new UserResponse(user.getId(), null, null);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.email() != null && !request.email().isBlank()) {
            // Check if email is already taken by another user
            if (userRepository.existsByEmailIgnoreCase(request.email())) {
                User existingUser = userRepository.findByEmailIgnoreCase(request.email())
                        .orElse(null);
                if (existingUser != null && !existingUser.getId().equals(id)) {
                    throw new ConflictException("Email already in use");
                }
            }
            user.setEmail(request.email());
        }
        userRepository.save(user);
        
        Profile profile = profileRepository.findByUserId(user.getId());
        String name = profile != null ? profile.getDisplayName() : null;
        String avatarUrl = profile != null ? profile.getAvatarUrl() : null;

        return new UserResponse(user.getId(), name, avatarUrl);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Profile profile = profileRepository.findByUserId(id);
        String name = profile != null ? profile.getDisplayName() : null;
        String avatarUrl = profile != null ? profile.getAvatarUrl() : null;

        return new UserResponse(user.getId(), name, avatarUrl);
    }

    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}