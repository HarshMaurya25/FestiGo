package com.example.FestiGo.service;

import com.example.FestiGo.domain.Users;
import com.example.FestiGo.dto.CompleteProfileRequest;
import com.example.FestiGo.dto.CreateUserRequest;
import com.example.FestiGo.dto.UserResponse;
import com.example.FestiGo.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        usersRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Email already in use");
        });

        Users user = new Users();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setProfileCompleted(false); // Step 1 only, profile not completed yet

        Users saved = usersRepository.save(user);
        return toUserResponse(saved);
    }

    public UserResponse authenticate(String email, String password) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(java.util.UUID id) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
                        "User not found"));
        return toUserResponse(u);
    }

    @Transactional
    public UserResponse completeProfile(java.util.UUID id, CompleteProfileRequest req) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
                        "User not found"));

        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setPhone(req.getPhone());
        u.setAddress(req.getAddress());
        u.setCity(req.getCity());
        u.setState(req.getState());
        u.setCountry(req.getCountry());
        u.setDob(req.getDob());
        u.setProfileCompleted(true);

        Users saved = usersRepository.save(u);
        return toUserResponse(saved);
    }

    @Transactional
    public UserResponse updateUserDetails(java.util.UUID id, com.example.FestiGo.dto.UserDetailsRequest req) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
                        "User not found"));
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setDob(req.getDob());
        Users saved = usersRepository.save(u);
        return toUserResponse(saved);
    }

    private UserResponse toUserResponse(Users u) {
        return new UserResponse(
                u.getId(), u.getEmail(), u.getRole(),
                u.getFirstName(), u.getLastName(), u.getDob(),
                u.getPhone(), u.getAddress(), u.getCity(),
                u.getState(), u.getCountry(),
                u.isProfileCompleted(), u.getCreatedAt());
    }
}
