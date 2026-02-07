package com.example.FestiGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private UUID bookingId;
    private UUID festId;
    private String festName;
    private LocalDate festDate;
    private LocalTime festTime;
    private String festImageUrl;
    private String city;
    private String place;
    private boolean attended;
    private LocalDateTime attendedAt;
    private LocalDateTime bookedAt;
    private String status; // "Confirmed", "Pending", "Cancelled"
}
