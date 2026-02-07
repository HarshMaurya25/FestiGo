package com.example.FestiGo.repository;

import com.example.FestiGo.domain.Fest;
import com.example.FestiGo.domain.FestAttendance;
import com.example.FestiGo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FestAttendanceRepository extends JpaRepository<FestAttendance, UUID> {
    long countByFestAndAttendedTrue(Fest fest);

    Optional<FestAttendance> findByFestAndUser(Fest fest, Users user);

    Optional<FestAttendance> findByFestIdAndUserId(UUID festId, UUID userId);

    List<FestAttendance> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserId(UUID userId);
}
