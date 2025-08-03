package com.app.flashcard.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationDTOTest {

    private Validator validator;
    private UserRegistrationDTO dto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        dto = new UserRegistrationDTO();
        dto.setLoginID("testuser");
        dto.setPassword("password123");
        dto.setName("Test User");
        dto.setAge(25);
        dto.setEmail("test@example.com");
    }

    @Test
    void testValidation_AllFieldsValid_Success() {
        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankLoginID_Error() {
        // Given
        dto.setLoginID("");

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loginID")));
    }

    @Test
    void testValidation_NullLoginID_Error() {
        // Given
        dto.setLoginID(null);

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("loginID")));
    }

    @Test
    void testValidation_ShortLoginID_Error() {
        // Given
        dto.setLoginID("ab"); // Less than 3 characters

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("loginID") && 
            v.getMessage().contains("3-50")));
    }

    @Test
    void testValidation_LongLoginID_Error() {
        // Given
        dto.setLoginID("a".repeat(51)); // More than 50 characters

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("loginID") && 
            v.getMessage().contains("3-50")));
    }

    @Test
    void testValidation_BlankPassword_Error() {
        // Given
        dto.setPassword("");

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testValidation_ShortPassword_Error() {
        // Given
        dto.setPassword("123"); // Less than 4 characters

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("password") && 
            v.getMessage().contains("4-100")));
    }

    @Test
    void testValidation_BlankName_Error() {
        // Given
        dto.setName("");

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testValidation_ShortName_Error() {
        // Given
        dto.setName("a"); // Less than 2 characters

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("name") && 
            v.getMessage().contains("2-100")));
    }

    @Test
    void testValidation_InvalidAge_TooYoung() {
        // Given
        dto.setAge(12); // Less than 13

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("age") && 
            v.getMessage().contains("13")));
    }

    @Test
    void testValidation_InvalidAge_TooOld() {
        // Given
        dto.setAge(121); // More than 120

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("age") && 
            v.getMessage().contains("120")));
    }

    @Test
    void testValidation_NullAge_Error() {
        // Given
        dto.setAge(null);

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("age")));
    }

    @Test
    void testValidation_BlankEmail_Error() {
        // Given
        dto.setEmail("");

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testValidation_InvalidEmail_Error() {
        // Given
        dto.setEmail("notanemail");

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") && 
            v.getMessage().contains("Email")));
    }

    @Test
    void testValidation_LongEmail_Error() {
        // Given
        dto.setEmail("a".repeat(90) + "@example.com"); // More than 100 characters

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") && 
            v.getMessage().contains("100")));
    }

    @Test
    void testValidation_MinimumValidValues() {
        // Given
        dto.setLoginID("abc"); // Minimum 3 chars
        dto.setPassword("1234"); // Minimum 4 chars
        dto.setName("Ab"); // Minimum 2 chars
        dto.setAge(13); // Minimum age
        dto.setEmail("a@b.c"); // Valid email

        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_MaximumValidValues() {
        // Given
        dto.setLoginID("a".repeat(50)); // Maximum 50 chars
        dto.setPassword("a".repeat(100)); // Maximum 100 chars
        dto.setName("a".repeat(100)); // Maximum 100 chars
        dto.setAge(120); // Maximum age
        /*
        Part Max Length
        Local-part 64 chars
        Domain 255 chars
        Total address 320 chars (practical limit)
        However settings @Size max = 100
        */
        dto.setEmail("a".repeat(64) + "@" + "b".repeat(30) +".com"); // Maximum 100 chars


        // When
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Validation should pass for maximum valid values, but got: " + violations);
    }
}