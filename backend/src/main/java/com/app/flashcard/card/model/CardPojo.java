package com.app.flashcard.card.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Card POJO for MyBatis - Clean model without JPA annotations
 * Using Lombok to reduce boilerplate code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPojo {
    
    // Primary key
    private Integer cardID;
    
    // Foreign key
    private Integer deckID;
    
    // Content fields
    private String frontContent;  // Fixed naming - was "FontContent" (typo in original)
    private String backContent;
    
    // Learning fields
    private LocalDate remindTime;
    
    @Builder.Default
    private Integer status = 0;  // 0: new, 1: learning, 2: due
    
    // Business logic methods
    public boolean isNew() {
        return status != null && status == 0;
    }
    
    public boolean isLearning() {
        return status != null && status == 1;
    }
    
    public boolean isDue() {
        return status != null && status == 2;
    }
    
    public boolean isDueToday() {
        return remindTime != null && !remindTime.isAfter(LocalDate.now());
    }
    
    // Convenience methods
    public void markAsLearning() {
        this.status = 1;
    }
    
    public void markAsDue() {
        this.status = 2;
    }
    
    public void setRemindTimeFromDaysFromNow(int days) {
        this.remindTime = LocalDate.now().plusDays(days);
    }
}