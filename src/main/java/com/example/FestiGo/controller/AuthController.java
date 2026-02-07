package com.example.FestiGo.controller;

import com.example.FestiGo.dto.LoginRequest;
import com.example.FestiGo.dto.UserResponse;
import com.example.FestiGo.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsersService usersService;

    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest req) {
        UserResponse resp = usersService.authenticate(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(resp);
    }
}
