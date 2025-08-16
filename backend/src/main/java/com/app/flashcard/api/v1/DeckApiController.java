package com.app.flashcard.api.v1;

import com.app.flashcard.api.dto.request.DeckCreateRequest;
import com.app.flashcard.api.dto.response.ApiResponse;
import com.app.flashcard.api.dto.response.DeckResponse;
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
@RequestMapping("/api/v1/decks")
@Tag(name = "Decks", description = "Deck management APIs")
public class DeckApiController {

    @Autowired
    private DeckService deckService;

    @Operation(summary = "Get all user decks", description = "Retrieve all decks for the authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeckResponse>>> getUserDecks(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<Deck> decks = deckService.findDecksByUserID(userPrincipal.getUserID().intValue());
            List<DeckResponse> deckResponses = decks.stream()
                .map(this::convertToDeckResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(deckResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve decks", e.getMessage()));
        }
    }

    @Operation(summary = "Get deck by ID", description = "Retrieve a specific deck by its ID")
    @GetMapping("/{deckId}")
    public ResponseEntity<ApiResponse<DeckResponse>> getDeck(
            @PathVariable Integer deckId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Deck deck = deckService.findByDeckID(deckId);
            
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Deck not found", "Deck does not exist or access denied"));
            }
            
            DeckResponse deckResponse = convertToDeckResponse(deck);
            return ResponseEntity.ok(ApiResponse.success(deckResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve deck", e.getMessage()));
        }
    }

    @Operation(summary = "Create new deck", description = "Create a new deck for the authenticated user")
    @PostMapping
    public ResponseEntity<ApiResponse<DeckResponse>> createDeck(
            @Valid @RequestBody DeckCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Deck deck = new Deck();
            deck.setUserID(userPrincipal.getUserID().intValue());
            deck.setDeckName(request.getDeckName());
            
            Deck savedDeck = deckService.save(deck);
            DeckResponse deckResponse = convertToDeckResponse(savedDeck);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(deckResponse, "Deck created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create deck", e.getMessage()));
        }
    }

    @Operation(summary = "Update deck", description = "Update an existing deck")
    @PutMapping("/{deckId}")
    public ResponseEntity<ApiResponse<DeckResponse>> updateDeck(
            @PathVariable Integer deckId,
            @Valid @RequestBody DeckCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Deck deck = deckService.findByDeckID(deckId);
            
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Deck not found", "Deck does not exist or access denied"));
            }
            
            deck.setDeckName(request.getDeckName());
            Deck updatedDeck = deckService.save(deck);
            DeckResponse deckResponse = convertToDeckResponse(updatedDeck);
            
            return ResponseEntity.ok(ApiResponse.success(deckResponse, "Deck updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update deck", e.getMessage()));
        }
    }

    @Operation(summary = "Delete deck", description = "Delete an existing deck")
    @DeleteMapping("/{deckId}")
    public ResponseEntity<ApiResponse<Void>> deleteDeck(
            @PathVariable Integer deckId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Deck deck = deckService.findByDeckID(deckId);
            
            if (deck == null || deck.getUserID() != userPrincipal.getUserID()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Deck not found", "Deck does not exist or access denied"));
            }
            
            deckService.deleteByDeckID(deckId);
            return ResponseEntity.ok(ApiResponse.success(null, "Deck deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete deck", e.getMessage()));
        }
    }

    private DeckResponse convertToDeckResponse(Deck deck) {
        return new DeckResponse(
            deck.getDeckID(),
            deck.getUserID(),
            deck.getDeckName(),
            deck.getNewCardNum(),
            deck.getLearningCardNum(),
            deck.getDueCardNum()
        );
    }
}