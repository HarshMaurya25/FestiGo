package com.example.FestiGo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateFestRequest {
    @NotBlank
    private String name;

    // ISO date (yyyy-MM-dd)
    private LocalDate date;
    // optional time
    private LocalTime time;

    private String imageUrl;
    private String description;
    private String genre;
    private String type;

    private String city;
    private String state;
    private String place;

    private Integer maxCapacity;

    // optional: set creator by id
    private UUID createdById;
}
