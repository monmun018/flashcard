package com.app.flashcard.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for card creation
 */
public class CardCreationDTO {
    
    @NotBlank(message = "Nội dung mặt trước không được để trống")
    @Size(min = 1, max = 1000, message = "Nội dung mặt trước phải từ 1-1000 ký tự")
    private String fontContent;
    
    @NotBlank(message = "Nội dung mặt sau không được để trống")
    @Size(min = 1, max = 1000, message = "Nội dung mặt sau phải từ 1-1000 ký tự")
    private String backContent;
    
    @NotNull(message = "Phải chọn bộ thẻ")
    private Integer deckID;

    public CardCreationDTO() {}

    public CardCreationDTO(String fontContent, String backContent, Integer deckID) {
        this.fontContent = fontContent;
        this.backContent = backContent;
        this.deckID = deckID;
    }

    public String getFontContent() {
        return fontContent;
    }

    public void setFontContent(String fontContent) {
        this.fontContent = fontContent;
    }

    public String getBackContent() {
        return backContent;
    }

    public void setBackContent(String backContent) {
        this.backContent = backContent;
    }

    public Integer getDeckID() {
        return deckID;
    }

    public void setDeckID(Integer deckID) {
        this.deckID = deckID;
    }
}