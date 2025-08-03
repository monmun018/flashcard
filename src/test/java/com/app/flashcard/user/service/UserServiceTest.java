package com.app.flashcard.user.service;

import com.app.flashcard.user.form.RegistForm;
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
    private RegistForm testRegistForm;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserID(1);
        testUser.setUserLoginID("testuser");
        testUser.setUserPW("hashedpassword");
        testUser.setUserName("Test User");
        testUser.setUserAge(25);
        testUser.setUserMail("test@example.com");

        testRegistForm = new RegistForm();
        testRegistForm.setLoginID("testuser");
        testRegistForm.setPw("password123");
        testRegistForm.setName("Test User");
        testRegistForm.setAge(25);
        testRegistForm.setMail("test@example.com");
    }

    @Test
    void testCreateUserWithHashedPassword_Success() {
        // Given
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUserWithHashedPassword(testRegistForm, passwordEncoder);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithHashedPassword_DuplicateLoginID() {
        // Given
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userService.createUserWithHashedPassword(testRegistForm, passwordEncoder));
        
        assertEquals("Tài khoản đã tồn tại.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testFindByLoginId_Success() {
        // Given
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        // When
        User result = userService.findByLoginId("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUserLoginID());
        assertEquals("Test User", result.getUserName());
    }

    @Test
    void testFindByLoginId_NotFound() {
        // Given
        when(userRepository.findByUserLoginID("nonexistent")).thenReturn(Collections.emptyList());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> userService.findByLoginId("nonexistent"));
        
        assertEquals("User not found with loginID: nonexistent", exception.getMessage());
    }

    @Test
    void testFindById_Success() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUserID());
        assertEquals("testuser", result.getUserLoginID());
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> userService.findById(999));
        
        assertEquals("User not found with ID: 999", exception.getMessage());
    }

    @Test
    void testUpdateProfile_Success() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        RegistForm updateForm = new RegistForm();
        updateForm.setPw("newpassword");
        updateForm.setName("Updated Name");
        updateForm.setAge(26);
        updateForm.setMail("updated@example.com");

        // When
        User result = userService.updateProfile(1, updateForm);

        // Then
        assertNotNull(result);
        verify(userRepository).save(testUser);
        assertEquals("newpassword", testUser.getUserPW());
        assertEquals("Updated Name", testUser.getUserName());
        assertEquals(26, testUser.getUserAge());
        assertEquals("updated@example.com", testUser.getUserMail());
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> userService.updateProfile(999, testRegistForm));
        
        assertEquals("User not found with ID: 999", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testIsLoginIDExists_True() {
        // Given
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        // When
        boolean result = userService.isLoginIDExists("testuser");

        // Then
        assertTrue(result);
    }

    @Test
    void testIsLoginIDExists_False() {
        // Given
        when(userRepository.findByUserLoginID("nonexistent")).thenReturn(Collections.emptyList());

        // When
        boolean result = userService.isLoginIDExists("nonexistent");

        // Then
        assertFalse(result);
    }

    @Test
    void testCreateRegistFormFromUser() {
        // When
        RegistForm result = userService.createRegistFormFromUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLoginID());
        assertEquals("hashedpassword", result.getPw());
        assertEquals("Test User", result.getName());
        assertEquals(25, result.getAge());
        assertEquals("test@example.com", result.getMail());
    }

    @Test
    void testCreateUser_Success() {
        // Given
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(testRegistForm);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_DuplicateLoginID() {
        // Given
        when(userRepository.findByUserLoginID("testuser")).thenReturn(Arrays.asList(testUser));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> userService.createUser(testRegistForm));
        
        assertEquals("Tài khoản đã tồn tại.", exception.getMessage());
    }
}