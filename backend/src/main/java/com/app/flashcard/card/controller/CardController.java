package com.app.flashcard.card.controller;

import com.app.flashcard.card.dto.CardCreationDTO;
import com.app.flashcard.card.service.CardService;
import com.app.flashcard.deck.service.DeckService;
import com.app.flashcard.shared.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Controller handling card management operations
 */
@Controller
@RequestMapping("")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private DeckService deckService;

    /**
     * Show add card form
     */
    @GetMapping("/cards/add")
    public String showAddCardForm(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                 ModelMap model) {
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("cardCreationDTO", new CardCreationDTO());
        Map<Integer, String> deckOptions = deckService.getDeckOptionsForUser(userPrincipal.getUserId());
        model.addAttribute("listOption", deckOptions);
        return "addCard";
    }

    /**
     * Legacy endpoint for backward compatibility
     */
    @GetMapping("/addCard")
    public String showAddCardFormLegacy(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                       ModelMap model) {
        return showAddCardForm(userPrincipal, model);
    }

    /**
     * Create new card
     */
    @PostMapping("/cards")
    public String createCard(@Valid @ModelAttribute("cardCreationDTO") CardCreationDTO dto,
                            BindingResult result,
                            ModelMap model,
                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (result.hasErrors()) {
            // Reload deck options for the form
            if (userPrincipal != null) {
                Map<Integer, String> deckOptions = deckService.getDeckOptionsForUser(userPrincipal.getUserId());
                model.addAttribute("listOption", deckOptions);
            }
            return "addCard";
        }
        
        // Map DTO to CardForm for service compatibility
        com.app.flashcard.card.form.CardForm form = mapDTOToCardForm(dto);
        cardService.createCard(form);
        
        return "redirect:/decks";
    }

    /**
     * Legacy endpoint for card creation (backward compatibility)
     * Note: The original endpoint used "deckCard" as the model attribute name
     */
    @PostMapping("/addCard")
    public String createCardLegacy(@Valid @ModelAttribute("deckCard") CardCreationDTO dto,
                                  BindingResult result,
                                  ModelMap model,
                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (result.hasErrors()) {
            // Reload deck options for the form
            if (userPrincipal != null) {
                Map<Integer, String> deckOptions = deckService.getDeckOptionsForUser(userPrincipal.getUserId());
                model.addAttribute("listOption", deckOptions);
            }
            return "addCard";
        }
        
        // Map DTO to CardForm for service compatibility
        com.app.flashcard.card.form.CardForm form = mapDTOToCardForm(dto);
        cardService.createCard(form);
        
        return "redirect:/decks";
    }

    // Private helper methods

    private com.app.flashcard.card.form.CardForm mapDTOToCardForm(CardCreationDTO dto) {
        com.app.flashcard.card.form.CardForm form = new com.app.flashcard.card.form.CardForm();
        form.setFontContent(dto.getFontContent());
        form.setBackContent(dto.getBackContent());
        form.setDeckID(dto.getDeckID());
        return form;
    }
}