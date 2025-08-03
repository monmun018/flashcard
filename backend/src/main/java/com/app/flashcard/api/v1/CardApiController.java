package com.app.flashcard.api.v1;

import com.app.flashcard.api.dto.request.CardCreateRequest;
import com.app.flashcard.api.dto.response.ApiResponse;
import com.app.flashcard.api.dto.response.CardResponse;
import com.app.flashcard.card.model.Card;
import com.app.flashcard.card.service.CardService;
import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.deck.service.DeckService;
import com.app.flashcard.shared.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "Card management APIs")
public class CardApiController {

    @Autowired
    private CardService cardService;

    @Autowired
    private DeckService deckService;

    @Operation(summary = "Get cards by deck", description = "Retrieve all cards for a specific deck")
    @GetMapping("/deck/{deckId}")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getCardsByDeck(
            @PathVariable Integer deckId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Deck deck = deckService.findByDeckID(deckId);
            
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Deck not found", "Deck does not exist or access denied"));
            }
            
            List<Card> cards = cardService.findCardsByDeckID(deckId);
            List<CardResponse> cardResponses = cards.stream()
                .map(this::convertToCardResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(cardResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve cards", e.getMessage()));
        }
    }

    @Operation(summary = "Get card by ID", description = "Retrieve a specific card by its ID")
    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> getCard(
            @PathVariable Integer cardId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Card card = cardService.findByCardID(cardId);
            
            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Card not found", "Card does not exist"));
            }
            
            Deck deck = deckService.findByDeckID(card.getDeckID());
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Card not found", "Card does not exist or access denied"));
            }
            
            CardResponse cardResponse = convertToCardResponse(card);
            return ResponseEntity.ok(ApiResponse.success(cardResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve card", e.getMessage()));
        }
    }

    @Operation(summary = "Create new card", description = "Create a new card in a deck")
    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @Valid @RequestBody CardCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Deck deck = deckService.findByDeckID(request.getDeckId());
            
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Deck not found", "Deck does not exist or access denied"));
            }
            
            Card card = new Card();
            card.setDeckID(request.getDeckId());
            card.setFontContent(request.getFrontContent());
            card.setBackContent(request.getBackContent());
            
            Card savedCard = cardService.save(card);
            CardResponse cardResponse = convertToCardResponse(savedCard);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cardResponse, "Card created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create card", e.getMessage()));
        }
    }

    @Operation(summary = "Update card", description = "Update an existing card")
    @PutMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @PathVariable Integer cardId,
            @Valid @RequestBody CardCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Card card = cardService.findByCardID(cardId);
            
            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Card not found", "Card does not exist"));
            }
            
            Deck deck = deckService.findByDeckID(card.getDeckID());
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Card not found", "Card does not exist or access denied"));
            }
            
            card.setFontContent(request.getFrontContent());
            card.setBackContent(request.getBackContent());
            
            Card updatedCard = cardService.save(card);
            CardResponse cardResponse = convertToCardResponse(updatedCard);
            
            return ResponseEntity.ok(ApiResponse.success(cardResponse, "Card updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update card", e.getMessage()));
        }
    }

    @Operation(summary = "Delete card", description = "Delete an existing card")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @PathVariable Integer cardId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Card card = cardService.findByCardID(cardId);
            
            if (card == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Card not found", "Card does not exist"));
            }
            
            Deck deck = deckService.findByDeckID(card.getDeckID());
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Card not found", "Card does not exist or access denied"));
            }
            
            cardService.deleteByCardID(cardId);
            return ResponseEntity.ok(ApiResponse.success(null, "Card deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete card", e.getMessage()));
        }
    }

    private CardResponse convertToCardResponse(Card card) {
        return new CardResponse(
            card.getCardID(),
            card.getDeckID(),
            card.getFontContent(),
            card.getBackContent(),
            card.getRemindTime(),
            card.getStatus()
        );
    }
}