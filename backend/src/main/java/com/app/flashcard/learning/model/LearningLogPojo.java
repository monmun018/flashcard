package com.app.flashcard.learning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningLogPojo {
    private Integer logID;
    private Integer deckID;
    private Integer userID;
    
    @Builder.Default
    private Integer learnTime = 1;
    
    @Builder.Default
    private LocalDate logTime = LocalDate.now();
}