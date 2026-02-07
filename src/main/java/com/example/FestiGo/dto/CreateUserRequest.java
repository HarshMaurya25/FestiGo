package com.example.FestiGo.dto;

import com.example.FestiGo.util.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Optional role chosen at signup
    private Roles role = Roles.USER;

    // Optional user details
    private String firstName;
    private String lastName;
    private java.time.LocalDate dob;
}
