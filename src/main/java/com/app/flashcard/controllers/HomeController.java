package com.app.flashcard.controllers;

import com.app.flashcard.card.form.CardForm;
import com.app.flashcard.deck.form.DeckForm;
import com.app.flashcard.user.form.LoginForm;
import com.app.flashcard.user.form.RegistForm;
import com.app.flashcard.card.model.Card;
import com.app.flashcard.deck.model.Deck;
import com.app.flashcard.learning.model.LearningLog;
import com.app.flashcard.user.model.User;
import com.app.flashcard.card.repository.CardRepository;
import com.app.flashcard.deck.repository.DeckRepository;
import com.app.flashcard.learning.repository.LearningLogRepository;
import com.app.flashcard.user.repository.UserRepository;
import com.app.flashcard.user.service.UserService;
import com.app.flashcard.deck.service.DeckService;
import com.app.flashcard.card.service.CardService;
import com.app.flashcard.learning.service.LearningService;
import com.app.flashcard.shared.security.UserPrincipal;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import com.app.flashcard.shared.exception.ValidationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Controller // Temporarily disabled to avoid mapping conflicts
//@RequestMapping(path = "")
// http:localhost:8080/
public class HomeController {
    @Autowired //Inject UserRepository (will be removed in later steps)
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private LearningLogRepository learningLogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DeckService deckService;

    @Autowired
    private CardService cardService;

    @Autowired
    private LearningService learningService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // http:localhost:8080/flashcard
    @RequestMapping(value = "/flashcard", method = RequestMethod.GET)
    public String getHomePage(ModelMap modelMap){
        Card card = cardService.findByFontContent("Hi");
        if (card != null) {
            modelMap.addAttribute("font",card.getFontContent());
            modelMap.addAttribute("back",card.getBackContent());
        } else {
            modelMap.addAttribute("font","Demo card");
            modelMap.addAttribute("back","Demo card back");
        }
        return "flashcard";
    }
    // http:localhost:8080/deckList
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String getAllDesk(@AuthenticationPrincipal UserPrincipal userPrincipal, ModelMap modelMap){
        if (userPrincipal != null) {
            List<Deck> decks = deckService.getDecksByUserWithStatistics(userPrincipal.getUserId());
            modelMap.addAttribute("decks", decks);
            modelMap.addAttribute("userName", userPrincipal.getDisplayName());
            return "home";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(ModelMap modelMap){
        modelMap.addAttribute("loginFrom",new LoginForm());
        return "login";
    }

    @RequestMapping(value = "/deck/{deckID}", method = RequestMethod.GET)
    public String showCard(ModelMap modelMap, @PathVariable int deckID,
                           HttpSession httpSession){
        Card card = cardService.getNextCardForDeck(deckID);
        if (card != null){
            modelMap.addAttribute("font",card.getFontContent());
            modelMap.addAttribute("back",card.getBackContent());
            // save ID to HTTP session
            httpSession.setAttribute("cardID", card.getCardID());
            httpSession.setAttribute("deckID", deckID);
            return "flashcard";
        }
        modelMap.addAttribute("font","Không có thẻ");
        modelMap.addAttribute("back","Không có thẻ");
        return "flashcard";
    }

    @RequestMapping(value = "/answer/{ans}", method = RequestMethod.POST)
    public String answerHandler(ModelMap modelMap, @PathVariable int ans,
                                @AuthenticationPrincipal UserPrincipal userPrincipal,
                                HttpSession httpSession){
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        //Get session info
        int userID = userPrincipal.getUserId();
        Integer cardID = (Integer) httpSession.getAttribute("cardID");
        Integer deckID = (Integer) httpSession.getAttribute("deckID");
        
        if (cardID == null || deckID == null) {
            return "redirect:/home";
        }

        // No card handler
        if (!cardService.hasDeckAnyCards(deckID)){
            return "redirect:../deck/"+deckID;
        }

        // Process the answer using LearningService
        learningService.processAnswer(cardID, ans, userID, deckID);
        
        return "redirect:../deck/"+deckID;
    }

    // Spring Security handles authentication automatically
    // No need for custom login handler

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage(ModelMap modelMap){
        modelMap.addAttribute("registFrom",new RegistForm());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String RegisterHandler(@Validated @ModelAttribute("registFrom") RegistForm form,
                                  BindingResult result,
                                  ModelMap modelMap){
        if (result.hasErrors()){
            return "register";
        }
        
        try {
            // Use new method with hashed password, pass passwordEncoder to avoid circular dependency
            User user = userService.createUserWithHashedPassword(form, passwordEncoder);
            modelMap.addAttribute("mess","Đăng ký thành công!");
            return "redirect:/login";
        } catch (ValidationException e) {
            modelMap.addAttribute("mess", e.getMessage());
            return "register";
        }
    }

    @RequestMapping(value = "/addDeck", method = RequestMethod.GET)
    public String addDeckForm(ModelMap modelMap){
        modelMap.addAttribute("deckForm",new DeckForm());
        return "addDeck";
    }

    @RequestMapping(value = "/addDeck", method = RequestMethod.POST)
    public String addDeckHandler(@Validated @ModelAttribute("deckForm") DeckForm form,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal,
                                 ModelMap modelMap){
        if (result.hasErrors()){
            return "addDeck";
        }
        
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        deckService.createDeck(form, userPrincipal.getUserId());
        return "redirect:/home";
    }

    // Spring Security handles logout automatically
    // No need for custom logout handler

    @RequestMapping(value = "/addCard", method = RequestMethod.GET)
    public String addCardForm(@AuthenticationPrincipal UserPrincipal userPrincipal,
                              ModelMap modelMap){
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        modelMap.addAttribute("cardForm",new CardForm());
        Map<Integer,String> deckOptions = deckService.getDeckOptionsForUser(userPrincipal.getUserId());
        modelMap.addAttribute("listOption",deckOptions);
        return "addCard";
    }

    @RequestMapping(value = "/addCard", method = RequestMethod.POST)
    public String addCardHandler(@Validated @ModelAttribute("deckCard") CardForm form,
                                 BindingResult result,
                                 ModelMap modelMap){
        if (result.hasErrors()){
            return "addCard";
        }
        
        cardService.createCard(form);
        return "redirect:/home";
    }

    @RequestMapping(value = "/deleteDeck/{deckID}", method = RequestMethod.GET)
    public String deleteDeck(@PathVariable int deckID){
        deckService.deleteDeck(deckID);
        return "redirect:../home";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                          ModelMap modelMap) {
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        User user = userPrincipal.getUser();
        RegistForm form = userService.createRegistFormFromUser(user);
        modelMap.addAttribute("registFrom",form);
        return "profile";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profileHandler(@Validated @ModelAttribute("registFrom") RegistForm form,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal,
                                 ModelMap modelMap) {
        if (result.hasErrors()){
            modelMap.addAttribute("mess","Thay đổi thất bại!");
            return "profile";
        }
        
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        try {
            User updatedUser = userService.updateProfile(userPrincipal.getUserId(), form);
            modelMap.addAttribute("mess","Thay đổi thành công!");
            return "profile";
        } catch (EntityNotFoundException e) {
            modelMap.addAttribute("mess","Thay đổi thất bại!");
            return "profile";
        }
    }
}
