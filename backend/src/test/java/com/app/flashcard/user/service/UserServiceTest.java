package com.app.flashcard.user.service;

import com.app.flashcard.user.model.User;
import com.app.flashcard.user.repository.UserRepository;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import com.app.flashcard.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserID(1);
        testUser.setUserLoginID("testuser");
        testUser.setUserPW("hashedpassword");
        testUser.setUserName("Test User");
        testUser.setUserAge(25);
        testUser.setUserMail("test@example.com");
    }

    @Test
    void testCreateUserWithHashedPassword_Success() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUserWithHashedPassword("testuser", "password123", "Test User", 25, "test@example.com", passwordEncoder);

        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
        assertEquals("Test User", result.getUserName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithHashedPassword_UserExists() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        assertThrows(ValidationException.class, 
            () -> userService.createUserWithHashedPassword("testuser", "password123", "Test User", 25, "test@example.com", passwordEncoder));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_Success() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        User result = userService.authenticate("testuser", "hashedpassword");

        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, 
            () -> userService.authenticate("testuser", "password"));
    }

    @Test
    void testAuthenticate_WrongPassword() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        assertThrows(ValidationException.class, 
            () -> userService.authenticate("testuser", "wrongpassword"));
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        User result = userService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getUserID());
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> userService.findById(999));
    }

    @Test
    void testUpdateProfile_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateProfile(1, "newpassword", "Updated Name", 26, "updated@example.com");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> userService.updateProfile(999, "newpassword", "Updated Name", 26, "updated@example.com"));
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("testuser", "password123", "Test User", 25, "test@example.com");

        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_UserExists() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        assertThrows(ValidationException.class, 
            () -> userService.createUser("testuser", "password123", "Test User", 25, "test@example.com"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testIsLoginIDExists_True() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        boolean result = userService.isLoginIDExists("testuser");

        assertTrue(result);
    }

    @Test
    void testIsLoginIDExists_False() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());

        boolean result = userService.isLoginIDExists("testuser");

        assertFalse(result);
    }

    @Test
    void testFindByLoginId_Success() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        User result = userService.findByLoginId("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
    }

    @Test
    void testFindByLoginId_NotFound() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, 
            () -> userService.findByLoginId("testuser"));
    }

    @Test
    void testFindByUserLoginID_Success() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        User result = userService.findByUserLoginID("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
    }

    @Test
    void testFindByUserLoginID_NotFound() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());

        User result = userService.findByUserLoginID("testuser");

        assertNull(result);
    }

    @Test
    void testExistsByUserLoginID_True() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        boolean result = userService.existsByUserLoginID("testuser");

        assertTrue(result);
    }

    @Test
    void testExistsByUserLoginID_False() {
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());

        boolean result = userService.existsByUserLoginID("testuser");

        assertFalse(result);
    }

    @Test
    void testSave() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.save(testUser);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).save(testUser);
    }
}