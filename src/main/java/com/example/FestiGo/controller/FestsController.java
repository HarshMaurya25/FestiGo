package com.example.FestiGo.controller;

import com.example.FestiGo.dto.CreateFestRequest;
import com.example.FestiGo.dto.FestResponse;
import com.example.FestiGo.service.FestsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fests")
public class FestsController {
    private final FestsService festsService;

    public FestsController(FestsService festsService) {
        this.festsService = festsService;
    }

    @PostMapping
    public ResponseEntity<FestResponse> createFest(@Valid @RequestBody CreateFestRequest req) {
        FestResponse resp = festsService.createFest(req);
        return ResponseEntity.created(URI.create("/api/fests/" + resp.getId())).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<FestResponse>> listFests() {
        return ResponseEntity.ok(festsService.listFests());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<FestResponse>> listUpcomingFests() {
        return ResponseEntity.ok(festsService.listUpcomingFests());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FestResponse>> getPopularFests(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(festsService.getPopularUpcomingFests(limit));
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<FestResponse>> getRecommendations(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(festsService.getPersonalizedRecommendations(userId, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FestResponse> getFest(@PathVariable UUID id) {
        return ResponseEntity.ok(festsService.getFest(id));
    }

    @PostMapping("/{id}/attend")
    public ResponseEntity<Void> attend(@PathVariable UUID id, @RequestBody com.example.FestiGo.dto.AttendRequest req) {
        festsService.attend(id, req.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/attend")
    public ResponseEntity<Void> unattend(@PathVariable UUID id,
            @RequestBody com.example.FestiGo.dto.AttendRequest req) {
        festsService.unattend(id, req.getUserId());
        return ResponseEntity.noContent().build();
    }
}
