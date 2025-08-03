package com.app.flashcard.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        logger.warn("Entity not found: {}", ex.getMessage());
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "The requested item was not found.");
        mav.addObject("status", HttpStatus.NOT_FOUND.value());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(ValidationException.class)
    public ModelAndView handleValidation(ValidationException ex, HttpServletRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "Invalid input provided.");
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("DTO validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "Form validation failed");
        mav.addObject("validationErrors", errors);
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(BindException.class)
    public ModelAndView handleBindingErrors(BindException ex, HttpServletRequest request) {
        logger.warn("Form binding error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "Form binding failed");
        mav.addObject("validationErrors", errors);
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "An unexpected error occurred.");
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }
}