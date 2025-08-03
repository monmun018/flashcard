package com.app.flashcard.learning.controller;

import com.app.flashcard.card.model.Card;
import com.app.flashcard.card.service.CardService;
import com.app.flashcard.learning.service.LearningService;
import com.app.flashcard.shared.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * Controller handling learning session and flashcard operations
 */
@Controller
@RequestMapping("")
public class LearningController {

    @Autowired
    private CardService cardService;

    @Autowired
    private LearningService learningService;

    /**
     * Demo flashcard page
     */
    @GetMapping("/flashcard")
    public String showDemoFlashcard(ModelMap model) {
        Card card = cardService.findByFontContent("Hi");
        if (card != null) {
            model.addAttribute("font", card.getFontContent());
            model.addAttribute("back", card.getBackContent());
        } else {
            model.addAttribute("font", "Demo card");
            model.addAttribute("back", "Demo card back");
        }
        return "flashcard";
    }

    /**
     * Start learning session for a deck
     */
    @GetMapping("/learn/deck/{deckId}")
    public String startLearningSession(@PathVariable int deckId,
                                      ModelMap model,
                                      HttpSession session) {
        Card card = cardService.getNextCardForDeck(deckId);
        if (card != null) {
            model.addAttribute("font", card.getFontContent());
            model.addAttribute("back", card.getBackContent());
            // Save session info
            session.setAttribute("cardID", card.getCardID());
            session.setAttribute("deckID", deckId);
            return "flashcard";
        }
        
        model.addAttribute("font", "Không có thẻ");
        model.addAttribute("back", "Không có thẻ");
        return "flashcard";
    }

    /**
     * Legacy endpoint for backward compatibility
     */
    @GetMapping("/deck/{deckID}")
    public String startLearningSessionLegacy(@PathVariable int deckID,
                                           ModelMap model,
                                           HttpSession session) {
        return startLearningSession(deckID, model, session);
    }

    /**
     * Process learning answer
     */
    @PostMapping("/learn/answer/{answer}")
    public String processAnswer(@PathVariable int answer,
                               @AuthenticationPrincipal UserPrincipal userPrincipal,
                               HttpSession session) {
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        // Get session info
        int userId = userPrincipal.getUserId();
        Integer cardId = (Integer) session.getAttribute("cardID");
        Integer deckId = (Integer) session.getAttribute("deckID");
        
        if (cardId == null || deckId == null) {
            return "redirect:/decks";
        }

        // Check if deck has any cards
        if (!cardService.hasDeckAnyCards(deckId)) {
            return "redirect:/learn/deck/" + deckId;
        }

        // Process the answer using LearningService
        learningService.processAnswer(cardId, answer, userId, deckId);
        
        return "redirect:/learn/deck/" + deckId;
    }

    /**
     * Legacy endpoint for backward compatibility
     */
    @PostMapping("/answer/{ans}")
    public String processAnswerLegacy(@PathVariable int ans,
                                     @AuthenticationPrincipal UserPrincipal userPrincipal,
                                     HttpSession session) {
        return processAnswer(ans, userPrincipal, session);
    }
}