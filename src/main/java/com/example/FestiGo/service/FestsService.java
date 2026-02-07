package com.example.FestiGo.service;

import com.example.FestiGo.domain.Fest;
import com.example.FestiGo.domain.FestAttendance;
import com.example.FestiGo.domain.Users;
import com.example.FestiGo.dto.BookingResponse;
import com.example.FestiGo.dto.CreateFestRequest;
import com.example.FestiGo.dto.FestResponse;
import com.example.FestiGo.repository.FestRepository;
import com.example.FestiGo.repository.FestAttendanceRepository;
import com.example.FestiGo.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FestsService {
    private final FestRepository festRepository;
    private final UsersRepository usersRepository;
    private final FestAttendanceRepository festAttendanceRepository;

    public FestsService(FestRepository festRepository, UsersRepository usersRepository,
            FestAttendanceRepository festAttendanceRepository) {
        this.festRepository = festRepository;
        this.usersRepository = usersRepository;
        this.festAttendanceRepository = festAttendanceRepository;
    }

    @Transactional
    public FestResponse createFest(CreateFestRequest req) {
        Fest fest = Fest.builder()
                .name(req.getName())
                .date(req.getDate())
                .time(req.getTime())
                .imageUrl(req.getImageUrl())
                .description(req.getDescription())
                .genre(req.getGenre())
                .type(req.getType())
                .city(req.getCity())
                .state(req.getState())
                .place(req.getPlace())
                .maxCapacity(req.getMaxCapacity())
                .build();

        if (req.getCreatedById() != null) {
            Users u = usersRepository.findById(req.getCreatedById())
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.BAD_REQUEST, "createdById not found"));
            fest.setCreatedBy(u);
        }

        Fest saved = festRepository.save(fest);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FestResponse> listFests() {
        return festRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FestResponse getFest(UUID id) {
        return festRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Fest not found"));
    }

    @Transactional
    public void attend(UUID festId, UUID userId) {
        Fest f = festRepository.findById(festId).orElseThrow(() -> new IllegalArgumentException("Fest not found"));

        // Check if fest has already happened
        if (f.getDate() != null && f.getDate().isBefore(java.time.LocalDate.now())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "This event has already ended");
        }

        // Check capacity
        if (f.getMaxCapacity() != null) {
            long currentAttendees = festAttendanceRepository.countByFestAndAttendedTrue(f);
            if (currentAttendees >= f.getMaxCapacity()) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "This event is fully booked");
            }
        }

        Users u = usersRepository.findById(userId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));
        FestAttendance att = festAttendanceRepository.findByFestAndUser(f, u).orElseGet(() -> {
            FestAttendance a = new FestAttendance();
            a.setFest(f);
            a.setUser(u);
            return a;
        });
        att.setAttended(true);
        att.setAttendedAt(java.time.LocalDateTime.now());
        festAttendanceRepository.save(att);
    }

    @Transactional
    public void unattend(UUID festId, UUID userId) {
        festAttendanceRepository.findByFestIdAndUserId(festId, userId).ifPresent(a -> {
            a.setAttended(false);
            a.setAttendedAt(null);
            festAttendanceRepository.save(a);
        });
    }

    private FestResponse toResponse(Fest f) {
        FestResponse r = new FestResponse();
        r.setId(f.getId());
        r.setName(f.getName());
        r.setDate(f.getDate());
        r.setTime(f.getTime());
        r.setImageUrl(f.getImageUrl());
        r.setDescription(f.getDescription());
        r.setGenre(f.getGenre());
        r.setType(f.getType());
        r.setCity(f.getCity());
        r.setState(f.getState());
        r.setPlace(f.getPlace());
        r.setMaxCapacity(f.getMaxCapacity());
        r.setCreatedAt(f.getCreatedAt());
        if (f.getCreatedBy() != null) {
            r.setCreatedById(f.getCreatedBy().getId());
            r.setCreatedByEmail(f.getCreatedBy().getEmail());
        }
        long attendees = festAttendanceRepository.countByFestAndAttendedTrue(f);
        r.setAttendeesCount((int) attendees);
        return r;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(UUID userId) {
        return festAttendanceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUserBookingsCount(UUID userId) {
        return festAttendanceRepository.countByUserId(userId);
    }

    private BookingResponse toBookingResponse(FestAttendance fa) {
        Fest f = fa.getFest();
        String status = fa.isAttended() ? "Confirmed" : "Pending";
        return BookingResponse.builder()
                .bookingId(fa.getId())
                .festId(f.getId())
                .festName(f.getName())
                .festDate(f.getDate())
                .festTime(f.getTime())
                .festImageUrl(f.getImageUrl())
                .city(f.getCity())
                .place(f.getPlace())
                .attended(fa.isAttended())
                .attendedAt(fa.getAttendedAt())
                .bookedAt(fa.getCreatedAt())
                .status(status)
                .build();
    }

    // Get popular upcoming fests (overall best)
    @Transactional(readOnly = true)
    public List<FestResponse> getPopularUpcomingFests(int limit) {
        return festRepository.findPopularUpcomingFests(java.time.LocalDate.now(), limit)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get personalized recommendations for a user
    @Transactional(readOnly = true)
    public List<FestResponse> getPersonalizedRecommendations(UUID userId, int limit) {
        return festRepository.findPersonalizedRecommendations(userId, java.time.LocalDate.now(), limit)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get upcoming fests only
    @Transactional(readOnly = true)
    public List<FestResponse> listUpcomingFests() {
        return festRepository.findUpcomingFests(java.time.LocalDate.now())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
