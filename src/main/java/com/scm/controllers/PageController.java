package com.scm.controllers;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scm.domain.Message;
import com.scm.domain.UserRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PageController {

    private final MessageSource messageSource;

    private static final String USER_FORM = "userForm";

    private static final String REGISTRATION_FAILED = "";
    private static final String RED = "";

    @RequestMapping({ "/home", "/" })
    public String home(Model model) {
        System.out.println("Home page handler");
        // sending data to view
        model.addAttribute("name", "Substring Technologies");
        model.addAttribute("youtubeChannel", "Learn Code With Vivek Pandey");
        model.addAttribute("githubRepo", "https://github.com/learncodewithdurgesh/");
        return "home";
    }

    // about route

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About page loading");
        return "about";
    }

    // services

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("services page loading");
        return "services";
    }

    // contact page

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping({ "/do-register" })
    public String doRegister(@Valid @ModelAttribute(USER_FORM) UserRequest userRequest, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).forEach(log::error);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm",
                    bindingResult);
            redirectAttributes.addFlashAttribute(USER_FORM, userRequest);
            String content = getMessage("user.registration.failed", LocaleContextHolder.getLocale(), "User Registration Failed");
            String color = getMessage("color.error", LocaleContextHolder.getLocale(), "red");
            Message errorMessage = Message.builder().content(content).color(color).build();
            redirectAttributes.addFlashAttribute("message", errorMessage);
            return "redirect:/register";
        }
        // TODO service call to save user in db
        
        String content = getMessage("user.registration.success", LocaleContextHolder.getLocale(), "User Registration Success");
        String color = getMessage("color.success", LocaleContextHolder.getLocale(), "green");
        Message errorMessage = Message.builder().content(content).color(color).build();
        redirectAttributes.addFlashAttribute("message", errorMessage);
        return "redirect:/login";
    }

    @GetMapping(path = { "/register" })
    public String register(Model model) {
        if (!model.containsAttribute(USER_FORM)) {
            model.addAttribute(USER_FORM, new UserRequest());
        }
        return "register";
    }

    private String getMessage(String key, Locale locale, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, locale);
    }
}
