package com.example.FestiGo.domain;

import com.example.FestiGo.util.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;

    // Profile fields
    private String firstName;
    private String lastName;
    private java.time.LocalDate dob;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;

    // Registration status
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean profileCompleted = false;

    private java.time.LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = java.time.LocalDateTime.now();
    }
}
