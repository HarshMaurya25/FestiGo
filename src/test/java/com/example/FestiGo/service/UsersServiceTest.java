package com.example.FestiGo.service;

import com.example.FestiGo.domain.Users;
import com.example.FestiGo.dto.CompleteProfileRequest;
import com.example.FestiGo.dto.CreateUserRequest;
import com.example.FestiGo.dto.UserResponse;
import com.example.FestiGo.repository.UsersRepository;
import com.example.FestiGo.util.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService usersService;

    private UUID testUserId;
    private Users testUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = new Users();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Roles.USER);
        testUser.setProfileCompleted(false);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setRole(Roles.USER);

        when(usersRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        UserResponse response = usersService.createUser(request);

        assertNotNull(response);
        assertEquals("new@example.com", response.getEmail());
        assertEquals(Roles.USER, response.getRole());
        assertFalse(response.isProfileCompleted());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void createUser_duplicateEmail_shouldThrowException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setRole(Roles.USER);

        when(usersRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(ResponseStatusException.class, () -> usersService.createUser(request));
        verify(usersRepository, never()).save(any());
    }

    @Test
    void authenticate_validCredentials_shouldReturnUser() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);

        UserResponse response = usersService.authenticate("test@example.com", "correctPassword");

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void authenticate_invalidEmail_shouldThrowException() {
        when(usersRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> usersService.authenticate("unknown@example.com", "password"));
    }

    @Test
    void authenticate_invalidPassword_shouldThrowException() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> usersService.authenticate("test@example.com", "wrongPassword"));
    }

    @Test
    void getUser_shouldReturnUserById() {
        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        UserResponse response = usersService.getUser(testUserId);

        assertNotNull(response);
        assertEquals(testUserId, response.getId());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void getUser_notFound_shouldThrowException() {
        when(usersRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> usersService.getUser(UUID.randomUUID()));
    }

    @Test
    void completeProfile_shouldUpdateUserProfile() {
        CompleteProfileRequest request = new CompleteProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("+911234567890");
        request.setAddress("123 Main St");
        request.setCity("Mumbai");
        request.setState("Maharashtra");
        request.setCountry("India");
        request.setDob(LocalDate.of(1990, 5, 15));

        when(usersRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse response = usersService.completeProfile(testUserId, request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Mumbai", response.getCity());
        assertTrue(response.isProfileCompleted());
    }

    @Test
    void completeProfile_userNotFound_shouldThrowException() {
        CompleteProfileRequest request = new CompleteProfileRequest();
        request.setFirstName("John");

        when(usersRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> usersService.completeProfile(UUID.randomUUID(), request));
    }
}
