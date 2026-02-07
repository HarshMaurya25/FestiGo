package com.example.FestiGo.dto;

import com.example.FestiGo.util.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private Roles role;

    // Profile details
    private String firstName;
    private String lastName;
    private java.time.LocalDate dob;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    
    private boolean profileCompleted;
    private java.time.LocalDateTime createdAt;
}
