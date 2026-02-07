package com.example.FestiGo.service;

import com.example.FestiGo.domain.Fest;
import com.example.FestiGo.domain.FestAttendance;
import com.example.FestiGo.domain.Users;
import com.example.FestiGo.dto.BookingResponse;
import com.example.FestiGo.dto.CreateFestRequest;
import com.example.FestiGo.dto.FestResponse;
import com.example.FestiGo.repository.FestAttendanceRepository;
import com.example.FestiGo.repository.FestRepository;
import com.example.FestiGo.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FestsServiceTest {

    @Mock
    private FestRepository festRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private FestAttendanceRepository festAttendanceRepository;

    @InjectMocks
    private FestsService festsService;

    private UUID testUserId;
    private UUID testFestId;
    private Users testUser;
    private Fest testFest;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testFestId = UUID.randomUUID();

        testUser = new Users();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");

        testFest = Fest.builder()
                .id(testFestId)
                .name("Test Festival")
                .date(LocalDate.of(2025, 6, 15))
                .time(LocalTime.of(18, 0))
                .description("A test festival")
                .genre("Music")
                .type("Concert")
                .city("Mumbai")
                .state("Maharashtra")
                .place("Venue A")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createFest_shouldCreateFestSuccessfully() {
        CreateFestRequest request = new CreateFestRequest();
        request.setName("New Festival");
        request.setDate(LocalDate.of(2025, 7, 20));
        request.setTime(LocalTime.of(19, 0));
        request.setDescription("A new festival");
        request.setGenre("Rock");
        request.setType("Outdoor");
        request.setCity("Delhi");
        request.setState("Delhi");
        request.setPlace("Stadium");

        when(festRepository.save(any(Fest.class))).thenAnswer(invocation -> {
            Fest f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });
        when(festAttendanceRepository.countByFestAndAttendedTrue(any())).thenReturn(0L);

        FestResponse response = festsService.createFest(request);

        assertNotNull(response);
        assertEquals("New Festival", response.getName());
        assertEquals("Delhi", response.getCity());
        verify(festRepository, times(1)).save(any(Fest.class));
    }

    @Test
    void createFest_withCreatedBy_shouldLinkUser() {
        CreateFestRequest request = new CreateFestRequest();
        request.setName("User Festival");
        request.setCreatedById(testUserId);

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(festRepository.save(any(Fest.class))).thenAnswer(invocation -> {
            Fest f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });
        when(festAttendanceRepository.countByFestAndAttendedTrue(any())).thenReturn(0L);

        FestResponse response = festsService.createFest(request);

        assertNotNull(response);
        assertEquals(testUserId, response.getCreatedById());
    }

    @Test
    void listFests_shouldReturnAllFests() {
        when(festRepository.findAll()).thenReturn(List.of(testFest));
        when(festAttendanceRepository.countByFestAndAttendedTrue(any())).thenReturn(5L);

        List<FestResponse> fests = festsService.listFests();

        assertEquals(1, fests.size());
        assertEquals("Test Festival", fests.get(0).getName());
        assertEquals(5, fests.get(0).getAttendeesCount());
    }

    @Test
    void getFest_shouldReturnFestById() {
        when(festRepository.findById(testFestId)).thenReturn(Optional.of(testFest));
        when(festAttendanceRepository.countByFestAndAttendedTrue(testFest)).thenReturn(10L);

        FestResponse response = festsService.getFest(testFestId);

        assertNotNull(response);
        assertEquals("Test Festival", response.getName());
        assertEquals(10, response.getAttendeesCount());
    }

    @Test
    void getFest_notFound_shouldThrowException() {
        when(festRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> festsService.getFest(UUID.randomUUID()));
    }

    @Test
    void attend_shouldCreateNewAttendance() {
        when(festRepository.findById(testFestId)).thenReturn(Optional.of(testFest));
        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(festAttendanceRepository.findByFestAndUser(testFest, testUser)).thenReturn(Optional.empty());
        when(festAttendanceRepository.save(any(FestAttendance.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> festsService.attend(testFestId, testUserId));

        verify(festAttendanceRepository, times(1)).save(any(FestAttendance.class));
    }

    @Test
    void attend_existingAttendance_shouldUpdateIt() {
        FestAttendance existingAttendance = new FestAttendance();
        existingAttendance.setFest(testFest);
        existingAttendance.setUser(testUser);
        existingAttendance.setAttended(false);

        when(festRepository.findById(testFestId)).thenReturn(Optional.of(testFest));
        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(festAttendanceRepository.findByFestAndUser(testFest, testUser))
                .thenReturn(Optional.of(existingAttendance));
        when(festAttendanceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        festsService.attend(testFestId, testUserId);

        assertTrue(existingAttendance.isAttended());
        assertNotNull(existingAttendance.getAttendedAt());
    }

    @Test
    void unattend_shouldSetAttendedToFalse() {
        FestAttendance attendance = new FestAttendance();
        attendance.setFest(testFest);
        attendance.setUser(testUser);
        attendance.setAttended(true);
        attendance.setAttendedAt(LocalDateTime.now());

        when(festAttendanceRepository.findByFestIdAndUserId(testFestId, testUserId))
                .thenReturn(Optional.of(attendance));
        when(festAttendanceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        festsService.unattend(testFestId, testUserId);

        assertFalse(attendance.isAttended());
        assertNull(attendance.getAttendedAt());
    }

    @Test
    void getUserBookings_shouldReturnUserBookings() {
        FestAttendance attendance = new FestAttendance();
        attendance.setId(UUID.randomUUID());
        attendance.setFest(testFest);
        attendance.setUser(testUser);
        attendance.setAttended(true);
        attendance.setCreatedAt(LocalDateTime.now());

        when(festAttendanceRepository.findByUserIdOrderByCreatedAtDesc(testUserId))
                .thenReturn(List.of(attendance));

        List<BookingResponse> bookings = festsService.getUserBookings(testUserId);

        assertEquals(1, bookings.size());
        assertEquals("Test Festival", bookings.get(0).getFestName());
        assertEquals("Confirmed", bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsCount_shouldReturnCount() {
        when(festAttendanceRepository.countByUserId(testUserId)).thenReturn(5L);

        long count = festsService.getUserBookingsCount(testUserId);

        assertEquals(5, count);
    }
}
