package com.app.flashcard.card.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CardCreationDTOTest {

    private Validator validator;
    private CardCreationDTO dto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        dto = new CardCreationDTO();
        dto.setFontContent("Hello");
        dto.setBackContent("Xin chào");
        dto.setDeckID(1);
    }

    @Test
    void testValidation_AllFieldsValid_Success() {
        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankFontContent_Error() {
        // Given
        dto.setFontContent("");

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fontContent")));
    }

    @Test
    void testValidation_NullFontContent_Error() {
        // Given
        dto.setFontContent(null);

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fontContent")));
    }

    @Test
    void testValidation_WhitespaceFontContent_Error() {
        // Given
        dto.setFontContent("   ");

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fontContent")));
    }

    @Test
    void testValidation_LongFontContent_Error() {
        // Given
        dto.setFontContent("a".repeat(1001)); // More than 1000 characters

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("fontContent") && 
            v.getMessage().contains("1-1000")));
    }

    @Test
    void testValidation_BlankBackContent_Error() {
        // Given
        dto.setBackContent("");

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("backContent")));
    }

    @Test
    void testValidation_NullBackContent_Error() {
        // Given
        dto.setBackContent(null);

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("backContent")));
    }

    @Test
    void testValidation_LongBackContent_Error() {
        // Given
        dto.setBackContent("a".repeat(1001)); // More than 1000 characters

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("backContent") && 
            v.getMessage().contains("1-1000")));
    }

    @Test
    void testValidation_NullDeckID_Error() {
        // Given
        dto.setDeckID(null);

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deckID")));
    }

    @Test
    void testValidation_MinimumValidContent() {
        // Given
        dto.setFontContent("A"); // Minimum 1 character
        dto.setBackContent("B"); // Minimum 1 character

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_MaximumValidContent() {
        // Given
        dto.setFontContent("a".repeat(1000)); // Maximum 1000 characters
        dto.setBackContent("b".repeat(1000)); // Maximum 1000 characters

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_SpecialCharactersInContent() {
        // Given
        dto.setFontContent("Hello! How are you? 你好吗？");
        dto.setBackContent("Xin chào! Bạn khỏe không? 😊");

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_MultilineContent() {
        // Given
        dto.setFontContent("Line 1\nLine 2\nLine 3");
        dto.setBackContent("Dòng 1\nDòng 2\nDòng 3");

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_HTMLContent() {
        // Given
        dto.setFontContent("<b>Bold text</b> and <i>italic text</i>");
        dto.setBackContent("<strong>Chữ đậm</strong> và <em>chữ nghiêng</em>");

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_NegativeDeckID() {
        // Given
        dto.setDeckID(-1);

        // When
        Set<ConstraintViolation<CardCreationDTO>> violations = validator.validate(dto);

        // Then
        // This should be valid since we only check for @NotNull, not positive values
        // In a real application, you might want to add @Positive validation
        assertTrue(violations.isEmpty());
    }
}