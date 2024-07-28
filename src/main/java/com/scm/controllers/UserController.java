package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.Helper;
import com.scm.repo.ContactRepo;
import com.scm.service.ContactService;
import com.scm.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ContactService contactService;

    // user dashbaord page

    @RequestMapping(value = "/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        log.info("User dashboard");
        if (authentication == null) {
            return "home";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        log.info("User logged in: {}", username);
        User user = userService.getUserByEmail(username);
        int contactSize = user.getContacts().size();
        long noOfFavorite = user.getContacts().stream().map(Contact::isFavorite).count();
        model.addAttribute("contactSize", contactSize);
        model.addAttribute("favorite", noOfFavorite);
        return "user/dashboard";
    }

    // user profile page

    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {

        return "user/profile";
    }

}
