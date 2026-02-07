package com.example.FestiGo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsRequest {
    private String firstName;
    private String lastName;
    private java.time.LocalDate dob;
}
