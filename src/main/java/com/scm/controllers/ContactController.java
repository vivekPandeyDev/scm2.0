package com.scm.controllers;

import java.util.Locale;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scm.domain.Message;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.form.ContactForm;
import com.scm.form.ContactSearchForm;
import com.scm.helper.AppConstant;
import com.scm.helper.Helper;
import com.scm.service.ContactService;
import com.scm.service.ImageService;
import com.scm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user/contacts")
@Slf4j
@RequiredArgsConstructor
public class ContactController {
    private final UserService userService;
    private final MessageSource messageSource;
    private final ContactService contactService;
    private final ImageService imageService;

    private static final String CONTACT_FORM = "contactForm";
    private static final String MESSAGE = "message";
    private static final String COLOR_GREEN = "green";

    @RequestMapping("/add")
    public String addContactView(Model model) {
        if(model.getAttribute(CONTACT_FORM) == null){
            ContactForm contactForm = new ContactForm();
            contactForm.setFavorite(true);
            model.addAttribute(CONTACT_FORM, contactForm);
        }
        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(
        @Valid @ModelAttribute(CONTACT_FORM) ContactForm contactForm, 
        BindingResult bindingResult,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        
        boolean isValid = true;    
        
        if(contactForm.getContactImage() == null || contactForm.getContactImage().isEmpty()){
            String messasge = getMessage("contact.empty.image", LocaleContextHolder.getLocale(), "Image cannot be empty");
            bindingResult.addError(new FieldError(CONTACT_FORM, "contactImage",messasge ));
            isValid = false;
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).forEach(log::error);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactForm",
                    bindingResult);
            redirectAttributes.addFlashAttribute(CONTACT_FORM, contactForm);
            isValid = false;
        }

        if(!isValid){
            String content = getMessage("contact.registration.failed", LocaleContextHolder.getLocale(),
            "Contact Registration Failed");
            String color = getMessage("color.error", LocaleContextHolder.getLocale(), "red");
            Message errorMessage = Message.builder().content(content).color(color).build();
            redirectAttributes.addFlashAttribute(MESSAGE, errorMessage);
            return "redirect:/user/contacts/add";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        Contact contact = getContactFromDtoAndUser(contactForm, username);
        // upload image to cloud service
        contact = uploadImageToCloudinary(contactForm, contact);
        // saving contact
        contactService.save(contact);
        log.info("contact form: {} " ,contactForm);

        String content = getMessage("contact.registration.success", LocaleContextHolder.getLocale(),
                "User Registration Success");
        String color = getMessage("color.success", LocaleContextHolder.getLocale(), COLOR_GREEN);
        Message errorMessage = Message.builder().content(content).color(color).build();
        redirectAttributes.addFlashAttribute(MESSAGE, errorMessage);              

        return "redirect:/user/contacts/add";

    }



    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstant.PAGE_SIZE + "" ) int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {

        // load all the user contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstant.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    @RequestMapping("/search")
    public String searchHandler(

            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstant.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        log.info("field {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        }

        log.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstant.PAGE_SIZE);

        return "user/search";
    }

     // detete contact
    @RequestMapping("/delete/{contactId}")
    public String deleteContact(
            @PathVariable("contactId") String contactId,
            RedirectAttributes redirectAttributes) {
        contactService.delete(contactId);
        log.info("contactId {} deleted", contactId);

        String content = getMessage("contact.delete.success", LocaleContextHolder.getLocale(),
                "User Registration Success");
       
        String color = getMessage("color.success", LocaleContextHolder.getLocale(), COLOR_GREEN);
        Message errorMessage = Message.builder().content(content).color(color).build();
        redirectAttributes.addFlashAttribute(MESSAGE, errorMessage);      

        return "redirect:/user/contacts";
    }

    // update contact form view
    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
            @PathVariable("contactId") String contactId,
            Model model) {

        var contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());
        model.addAttribute(CONTACT_FORM, contactForm);
        model.addAttribute("contactId", contactId);

        return "user/update_contact_view";
    }

    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            BindingResult bindingResult,
            Model model) {

        // update the contact
        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        var con = contactService.getById(contactId);
        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setFavorite(contactForm.isFavorite());
        con.setWebsiteLink(contactForm.getWebsiteLink());
        con.setLinkedInLink(contactForm.getLinkedInLink());

        // process image:

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            log.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);

        } else {
            log.info("file is empty");
        }

        var updateCon = contactService.update(con);
        log.info("updated contact {}", updateCon);

        model.addAttribute(MESSAGE, Message.builder().content("Contact Updated !!").color(COLOR_GREEN).build());

        return "redirect:/user/contacts/view/" + contactId;
    }


    private String getMessage(String key, Locale locale, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, locale);
    }


    private Contact getContactFromDtoAndUser(ContactForm contactForm, String username) {
        User user = userService.getUserByEmail(username);
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        return contact;
    }

    private Contact uploadImageToCloudinary(ContactForm contactForm, Contact contact) {
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            // upload file to cloudinary 
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);

        }
        return contact;
    }
}
