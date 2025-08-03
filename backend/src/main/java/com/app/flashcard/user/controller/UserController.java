package com.app.flashcard.user.controller;

import com.app.flashcard.user.dto.UserRegistrationDTO;
import com.app.flashcard.user.dto.UserProfileDTO;
import com.app.flashcard.user.dto.LoginDTO;
import com.app.flashcard.user.model.User;
import com.app.flashcard.user.service.UserService;
import com.app.flashcard.shared.security.UserPrincipal;
import com.app.flashcard.shared.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller handling user authentication and profile management
 */
@Controller
@RequestMapping("")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Show login form
     */
    @GetMapping("/login")
    public String showLoginForm(ModelMap model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    /**
     * Show registration form
     */
    @GetMapping("/register")
    public String showRegistrationForm(ModelMap model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "register";
    }

    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO dto,
                              BindingResult result,
                              ModelMap model) {
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            // Map DTO to User and create with hashed password
            User user = mapRegistrationDTOToUser(dto);
            userService.createUserWithHashedPassword(mapToRegistForm(dto), passwordEncoder);
            
            model.addAttribute("mess", "Đăng ký thành công!");
            return "redirect:/login";
        } catch (ValidationException e) {
            model.addAttribute("mess", e.getMessage());
            return "register";
        }
    }

    /**
     * Show user profile form
     */
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             ModelMap model) {
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        User user = userPrincipal.getUser();
        UserProfileDTO dto = mapUserToProfileDTO(user);
        model.addAttribute("userProfileDTO", dto);
        return "profile";
    }

    /**
     * Handle profile update
     */
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userProfileDTO") UserProfileDTO dto,
                               BindingResult result,
                               @AuthenticationPrincipal UserPrincipal userPrincipal,
                               ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Thay đổi thất bại!");
            return "profile";
        }
        
        if (userPrincipal == null) {
            return "redirect:/login";
        }
        
        try {
            // Convert DTO to RegistForm for service compatibility
            com.app.flashcard.user.form.RegistForm form = mapProfileDTOToRegistForm(dto);
            userService.updateProfile(userPrincipal.getUserId(), form);
            
            model.addAttribute("mess", "Thay đổi thành công!");
            return "profile";
        } catch (Exception e) {
            model.addAttribute("mess", "Thay đổi thất bại!");
            return "profile";
        }
    }

    // Private helper methods for mapping between DTOs and entities

    private User mapRegistrationDTOToUser(UserRegistrationDTO dto) {
        User user = new User();
        user.setUserLoginID(dto.getLoginID());
        user.setUserPW(dto.getPassword()); // Will be hashed in service
        user.setUserName(dto.getName());
        user.setUserAge(dto.getAge());
        user.setUserMail(dto.getEmail());
        return user;
    }

    private UserProfileDTO mapUserToProfileDTO(User user) {
        return new UserProfileDTO(
            user.getUserLoginID(),
            user.getUserPW(),
            user.getUserName(),
            user.getUserAge(),
            user.getUserMail()
        );
    }

    private com.app.flashcard.user.form.RegistForm mapToRegistForm(UserRegistrationDTO dto) {
        com.app.flashcard.user.form.RegistForm form = new com.app.flashcard.user.form.RegistForm();
        form.setLoginID(dto.getLoginID());
        form.setPw(dto.getPassword());
        form.setName(dto.getName());
        form.setAge(dto.getAge());
        form.setMail(dto.getEmail());
        return form;
    }

    private com.app.flashcard.user.form.RegistForm mapProfileDTOToRegistForm(UserProfileDTO dto) {
        com.app.flashcard.user.form.RegistForm form = new com.app.flashcard.user.form.RegistForm();
        form.setLoginID(dto.getLoginID());
        form.setPw(dto.getPassword());
        form.setName(dto.getName());
        form.setAge(dto.getAge());
        form.setMail(dto.getEmail());
        return form;
    }
}