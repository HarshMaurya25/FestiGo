package com.example.FestiGo.controller;

import com.example.FestiGo.dto.BookingResponse;
import com.example.FestiGo.dto.CompleteProfileRequest;
import com.example.FestiGo.dto.CreateUserRequest;
import com.example.FestiGo.dto.UserDetailsRequest;
import com.example.FestiGo.dto.UserResponse;
import com.example.FestiGo.service.FestsService;
import com.example.FestiGo.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;
    private final FestsService festsService;

    public UsersController(UsersService usersService, FestsService festsService) {
        this.usersService = usersService;
        this.festsService = festsService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest req) {
        UserResponse resp = usersService.createUser(req);
        // return id and role so frontend can store
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(usersService.getUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable java.util.UUID id,
            @RequestBody UserDetailsRequest req) {
        UserResponse resp = usersService.updateUserDetails(id, req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/complete-profile")
    public ResponseEntity<UserResponse> completeProfile(@PathVariable java.util.UUID id,
            @Valid @RequestBody CompleteProfileRequest req) {
        UserResponse resp = usersService.completeProfile(id, req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable java.util.UUID id) {
        List<BookingResponse> bookings = festsService.getUserBookings(id);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}/bookings/count")
    public ResponseEntity<Long> getUserBookingsCount(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(festsService.getUserBookingsCount(id));
    }
}
