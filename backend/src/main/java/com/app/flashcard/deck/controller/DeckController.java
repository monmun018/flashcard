package com.app.flashcard.deck.controller;

import com.app.flashcard.deck.dto.DeckCreationDTO;
import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.deck.service.DeckService;
import com.app.flashcard.shared.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller handling deck management operations
 */
@Controller
@RequestMapping("")
public class DeckController {

    @Autowired
    private DeckService deckService;

    /**
     * Home page - redirect to deck list
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/decks";
    }

    /**
     * Alternative home endpoint - redirect to deck list
     */
    @GetMapping("/home")
    public String homePage() {
        return "redirect:/decks";
    }

    /**
     * List user's decks
     */
    @GetMapping("/decks")
    public String listDecks(@AuthenticationPrincipal UserPrincipal userPrincipal,
                           ModelMap model) {
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        List<Deck> decks = deckService.getDecksByUserWithStatistics(userPrincipal.getUserId());
        model.addAttribute("decks", decks);
        model.addAttribute("userName", userPrincipal.getDisplayName());
        return "home"; // Still using the existing home template
    }

    /**
     * Show add deck form
     */
    @GetMapping("/decks/add")
    public String showAddDeckForm(ModelMap model) {
        model.addAttribute("deckCreationDTO", new DeckCreationDTO());
        return "addDeck";
    }

    /**
     * Alternative endpoint for backward compatibility
     */
    @GetMapping("/addDeck")
    public String showAddDeckFormLegacy(ModelMap model) {
        return showAddDeckForm(model);
    }

    /**
     * Create new deck
     */
    @PostMapping("/decks")
    public String createDeck(@Valid @ModelAttribute("deckCreationDTO") DeckCreationDTO dto,
                            BindingResult result,
                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                            ModelMap model) {
        if (result.hasErrors()) {
            return "addDeck";
        }
        
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        // Map DTO to DeckForm for service compatibility
        com.app.flashcard.deck.form.DeckForm form = mapDTOToDeckForm(dto);
        deckService.createDeck(form, userPrincipal.getUserId());
        
        return "redirect:/decks";
    }

    /**
     * Legacy endpoint for deck creation (backward compatibility)
     */
    @PostMapping("/addDeck")
    public String createDeckLegacy(@Valid @ModelAttribute("deckCreationDTO") DeckCreationDTO dto,
                                  BindingResult result,
                                  @AuthenticationPrincipal UserPrincipal userPrincipal,
                                  ModelMap model) {
        return createDeck(dto, result, userPrincipal, model);
    }

    /**
     * Delete deck (web-friendly GET endpoint)
     */
    @GetMapping("/decks/{deckId}/delete")
    public String deleteDeck(@PathVariable int deckId) {
        deckService.deleteDeck(deckId);
        return "redirect:/decks";
    }

    /**
     * Legacy delete endpoint for backward compatibility
     */
    @GetMapping("/deleteDeck/{deckID}")
    public String deleteDeckLegacy(@PathVariable int deckID) {
        return deleteDeck(deckID);
    }

    /**
     * RESTful delete endpoint (for future API use)
     */
    @DeleteMapping("/decks/{deckId}")
    public String deleteDeckREST(@PathVariable int deckId) {
        deckService.deleteDeck(deckId);
        return "redirect:/decks";
    }

    // Private helper methods

    private com.app.flashcard.deck.form.DeckForm mapDTOToDeckForm(DeckCreationDTO dto) {
        com.app.flashcard.deck.form.DeckForm form = new com.app.flashcard.deck.form.DeckForm();
        form.setDeckName(dto.getDeckName());
        return form;
    }
}