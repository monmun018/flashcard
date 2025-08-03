package com.app.flashcard.deck.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckCreationDTOTest {

    private Validator validator;
    private DeckCreationDTO dto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        dto = new DeckCreationDTO();
        dto.setDeckName("Test Deck");
    }

    @Test
    void testValidation_ValidDeckName_Success() {
        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankDeckName_Error() {
        // Given
        dto.setDeckName("");

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deckName")));
    }

    @Test
    void testValidation_NullDeckName_Error() {
        // Given
        dto.setDeckName(null);

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deckName")));
    }

    @Test
    void testValidation_WhitespaceDeckName_Error() {
        // Given
        dto.setDeckName("   ");

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deckName")));
    }

    @Test
    void testValidation_LongDeckName_Error() {
        // Given
        dto.setDeckName("a".repeat(101)); // More than 100 characters

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("deckName") && 
            v.getMessage().contains("1-100")));
    }

    @Test
    void testValidation_MinimumValidDeckName() {
        // Given
        dto.setDeckName("A"); // Minimum 1 character

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_MaximumValidDeckName() {
        // Given
        dto.setDeckName("a".repeat(100)); // Maximum 100 characters

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_SpecialCharactersInDeckName() {
        // Given
        dto.setDeckName("Deck with special chars: áàảãạ êếềễệ 123 !@#");

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_UnicodeCharactersInDeckName() {
        // Given
        dto.setDeckName("Bộ thẻ tiếng Việt 🎓📚");

        // When
        Set<ConstraintViolation<DeckCreationDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }
}