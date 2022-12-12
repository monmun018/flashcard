package com.app.flashcard.controllers;

import com.app.flashcard.forms.CardForm;
import com.app.flashcard.forms.DeckForm;
import com.app.flashcard.forms.LoginForm;
import com.app.flashcard.forms.RegistForm;
import com.app.flashcard.models.Card;
import com.app.flashcard.models.Deck;
import com.app.flashcard.models.LearningLog;
import com.app.flashcard.models.User;
import com.app.flashcard.repositories.CardRepository;
import com.app.flashcard.repositories.DeckRepository;
import com.app.flashcard.repositories.LearningLogRepository;
import com.app.flashcard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "")
@SessionAttributes("Ses")
// http:localhost:8080/
public class HomeController {
    @Autowired //Inject UserRepository
    private UserRepository userRepository;
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private LearningLogRepository learningLogRepository;

    @ModelAttribute("Ses")
    public Session getLoginSession(){
        return new Session();
    }

    // http:localhost:8080/flashcard
    @RequestMapping(value = "/flashcard", method = RequestMethod.GET)
    public String getHomePage(ModelMap modelMap){
        List<Card> list = cardRepository.findByFontContent("Hi");
        Card card = list.iterator().next();
        modelMap.addAttribute("font",card.getFontContent());
        modelMap.addAttribute("back",card.getBackContent());
        return "flashcard";
    }
    // http:localhost:8080/deckList
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String getAllDesk(@ModelAttribute("Ses") Session session,ModelMap modelMap){
        if (session.isLogin()){
            List<Deck> decks = deckRepository.findByUserID(session.getUser().getUserID());
            for (Deck d : decks){
                int deckID = d.getDeckID();
                int newCardNum = cardRepository.countNewCardNum(deckID);
                int learningCardNum = cardRepository.countLearningCardNum(deckID);
                int dueCardNum = cardRepository.countDueCardNum(deckID);
                d.setNewCardNum(newCardNum);
                d.setLearningCardNum(learningCardNum);
                d.setDueCardNum(dueCardNum);
            }
            deckRepository.saveAll(decks);
            modelMap.addAttribute("decks",decks);
            modelMap.addAttribute("userName",session.getUser().getUserName());
            return "home";
        }
        return "redirect:./login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(ModelMap modelMap){
        modelMap.addAttribute("loginFrom",new LoginForm());
        return "login";
    }

    @RequestMapping(value = "/deck/{deckID}", method = RequestMethod.GET)
    public String showCard(ModelMap modelMap,@PathVariable int deckID,
                           @ModelAttribute("Ses") Session session){
        Iterator<Card> cards = cardRepository.findByDeckIDOrderByRemindTimeAsc(deckID).iterator();
        if (cards.hasNext()){
            Card card = cards.next();
            modelMap.addAttribute("font",card.getFontContent());
            modelMap.addAttribute("back",card.getBackContent());
            // save ID to session
            session.setCardID(card.getCardID());
            session.setDeckID(deckID);
            return "flashcard";
        }
        modelMap.addAttribute("font","Không có thẻ");
        modelMap.addAttribute("back","Không có thẻ");
        return "flashcard";
    }

    @RequestMapping(value = "/answer/{ans}", method = RequestMethod.POST)
    public String answerHandler(ModelMap modelMap, @PathVariable int ans,
                                @ModelAttribute("Ses") Session session){
        //Get session info
        int userID = session.getUser().getUserID();
        int cardID = session.getCardID();
        int deckID = session.getDeckID();

        // No card handler
        if (cardRepository.countCardByDeckID(deckID) == 0){
            return "redirect:../deck/"+deckID;
        }

        //Card handler->Update Status
        Card card = cardRepository.findById(cardID).get();
        int statusTemp = ans == 1 ? 0 : card.getStatus()+ans;
        card.setStatus(statusTemp);

        //Card handler->Update remind time
        if (ans == 1 && card.getRemindTime().isAfter(LocalDate.now())){
            card.setRemindTime(LocalDate.now());
        }
        card.setRemindTime(card.getRemindTime().plusDays(statusTemp));
        cardRepository.save(card);

        //Log Handler
        //Log Handler --> Learning during the day
        //                else --> new Log
        Iterator<LearningLog> logs = learningLogRepository
                .findByDeckIDAndUserIDAndLogTime(deckID,userID,LocalDate.now())
                .iterator();
        LearningLog log;
        if(logs.hasNext()){
            log = logs.next();
            log.increaseLearnTime();
        } else{
            log = new LearningLog();
            //logID auto gen
            log.setDeckID(deckID);
            log.setUserID(userID);
        }
        learningLogRepository.save(log);
        return "redirect:../deck/"+deckID;
    }

    @RequestMapping(value = "/loginHandler", method = RequestMethod.POST)
    public String loginHandler(@Validated @ModelAttribute("loginFrom") LoginForm form,
                               BindingResult result,
                               @ModelAttribute("Ses") Session session,
                               ModelMap modelMap) {
        if (result.hasErrors()){
            return "login";
        }
        String id = form.getLoginID();
        String pw = form.getPw();
        User user = userRepository.findByUserLoginID(id).iterator().next();
        if(user.getUserPW().equals(pw)) {
            session.setUser(user);
            session.setLogin(true);
            return "redirect:./home";
        }
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage(@ModelAttribute("Ses") Session session,
                                  ModelMap modelMap){
        modelMap.addAttribute("registFrom",new RegistForm());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String RegisterHandler(@Validated @ModelAttribute("registFrom") RegistForm form,
                                  BindingResult result,
                                  @ModelAttribute("Ses") Session session,
                                  ModelMap modelMap){
        if (result.hasErrors()){
            return "register";
        }
        try {
            //Xử lý trùng tài khoản
            if(userRepository.findByUserLoginID(form.getLoginID()).iterator().hasNext()){
                modelMap.addAttribute("mess","Tài khoản đã tồn tại.");
                return "register";
            }
            // Tạo user và lưu lại <Now task>
            User user = new User().setByRegistForm(form);
            userRepository.save(user);
        } catch (Exception e){
            modelMap.addAttribute("mess","Đăng ký bị lỗi!");
            return "redirect:./register";
        }
        modelMap.addAttribute("mess","Đăng ký thành công!");
        return "redirect:./login";
    }

    @RequestMapping(value = "/addDeck", method = RequestMethod.GET)
    public String addDeckForm(@ModelAttribute("Ses") Session session,
                              ModelMap modelMap){
        modelMap.addAttribute("deckForm",new DeckForm());
        return "addDeck";
    }

    @RequestMapping(value = "/addDeck", method = RequestMethod.POST)
    public String addDeckHandler(@Validated @ModelAttribute("deckForm") DeckForm form,
                                 BindingResult result,
                                 @ModelAttribute("Ses") Session session,
                              ModelMap modelMap){
        if (result.hasErrors()){
            return "addDeck";
        }
        String name = form.getDeckName();
        int userID = session.getUser().getUserID();
        Deck newDeck = new Deck();
        newDeck.setDeckName(name);
        newDeck.setUserID(userID);
        deckRepository.save(newDeck);
        return "redirect:./home";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutHandler(@ModelAttribute("Ses") Session session,
                                ModelMap modelMap){
        session.setLogin(false);
        session.setUser(null);
        session.setCardID(-1);
        session.setDeckID(-1);
        return "redirect:./login";
    }

    @RequestMapping(value = "/addCard", method = RequestMethod.GET)
    public String addCardForm(@ModelAttribute("Ses") Session session,
                              ModelMap modelMap){
        modelMap.addAttribute("cardForm",new CardForm());
        List<Deck> decks = deckRepository.findByUserID(session.getUser().getUserID());
        Map<Integer,String> map = decks.stream().collect(Collectors.toMap(Deck::getDeckID,Deck::getDeckName));
        modelMap.addAttribute("listOption",map);
        return "addCard";
    }

    @RequestMapping(value = "/addCard", method = RequestMethod.POST)
    public String addCardHandler(@Validated @ModelAttribute("deckCard") CardForm form,
                                 BindingResult result,
                                 @ModelAttribute("Ses") Session session,
                                 ModelMap modelMap){
        if (result.hasErrors()){
            return "addCard";
        }
        int deskID = (int) form.getDeckID();
        String fontContent = form.getFontContent();
        String backContent = form.getBackContent();
        Card newCard = new Card();
        newCard.setStatus(0);
        newCard.setDeckID(deskID);
        newCard.setFontContent(fontContent);
        newCard.setBackContent(backContent);
        cardRepository.save(newCard);
        return "redirect:./home";
    }

    @RequestMapping(value = "/deleteDeck/{deckID}", method = RequestMethod.GET)
    public String deleteDeck(@PathVariable int deckID){
        List<Card> cards = cardRepository.findByDeckIDOrderByRemindTimeAsc(deckID);
        cardRepository.deleteAll(cards);
        deckRepository.deleteById(deckID);
        return "redirect:../home";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(@ModelAttribute("Ses") Session session,
                          ModelMap modelMap) {
        User user = session.getUser();
        RegistForm form = new RegistForm();
        form.setLoginID(user.getUserLoginID());
        form.setPw(user.getUserPW());
        form.setName(user.getUserName());
        form.setAge(user.getUserAge());
        form.setMail(user.getUserMail());
        modelMap.addAttribute("registFrom",form);
        return "profile";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profileHandler(@Validated @ModelAttribute("registFrom") RegistForm form,
                                 BindingResult result,
                                 @ModelAttribute("Ses") Session session,
                                 ModelMap modelMap) {
        if (result.hasErrors()){
            modelMap.addAttribute("mess","Thay đổi thất bại!");
            return "profile";
        }
        User user = userRepository.findById(session.getUser().getUserID()).get();
        user.setUserPW(form.getPw());
        user.setUserName(form.getName());
        user.setUserAge(form.getAge());
        user.setUserMail(form.getMail());
        userRepository.save(user);
        session.setUser(user);
        modelMap.addAttribute("mess","Thay đổi thành công!");
        return "profile";
    }
}
