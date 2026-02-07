package com.example.FestiGo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FestResponse {
    private UUID id;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private String imageUrl;
    private String description;
    private String genre;
    private String type;
    private String city;
    private String state;
    private String place;
    private UUID createdById;
    private String createdByEmail;
    private int attendeesCount;
    private Integer maxCapacity;
    private LocalDateTime createdAt;
    // optional flag for current user views
    private Boolean attended;
}
