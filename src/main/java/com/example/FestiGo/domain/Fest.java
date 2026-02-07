package com.example.FestiGo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fests")
public class Fest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private LocalDate date;
    private LocalTime time;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "text")
    private String description;

    private String genre;
    private String type;

    private String city;
    private String state;
    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Users createdBy;

    @OneToMany(mappedBy = "fest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FestAttendance> attendances = new HashSet<>();

    private Integer maxCapacity;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }
}
