package com.example.FestiGo.controller;

import com.example.FestiGo.dto.FestResponse;
import com.example.FestiGo.service.FestsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FestsControllerTest {

    @Mock
    private FestsService festsService;

    @InjectMocks
    private FestsController festsController;

    @Test
    void listFests_shouldReturnEmptyList() {
        when(festsService.listFests()).thenReturn(Collections.emptyList());

        ResponseEntity<List<FestResponse>> response = festsController.listFests();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void listFests_shouldReturnFests() {
        FestResponse fest = new FestResponse();
        fest.setId(UUID.randomUUID());
        fest.setName("Summer Festival");
        fest.setDate(LocalDate.of(2025, 6, 15));
        fest.setTime(LocalTime.of(18, 0));
        fest.setCity("Mumbai");
        fest.setAttendeesCount(100);
        fest.setCreatedAt(LocalDateTime.now());

        when(festsService.listFests()).thenReturn(List.of(fest));

        ResponseEntity<List<FestResponse>> response = festsController.listFests();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Summer Festival", response.getBody().get(0).getName());
    }

    @Test
    void getFest_shouldReturnFestById() {
        UUID festId = UUID.randomUUID();

        FestResponse fest = new FestResponse();
        fest.setId(festId);
        fest.setName("Rock Concert");
        fest.setCity("Delhi");
        fest.setAttendeesCount(250);

        when(festsService.getFest(festId)).thenReturn(fest);

        ResponseEntity<FestResponse> response = festsController.getFest(festId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Rock Concert", response.getBody().getName());
        assertEquals("Delhi", response.getBody().getCity());
    }
}