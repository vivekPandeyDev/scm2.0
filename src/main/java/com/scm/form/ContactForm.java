package com.scm.form;

import org.springframework.web.multipart.MultipartFile;

import com.scm.validators.ValidFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContactForm {

    @NotBlank(message = "{user.blank.name}")
    private String name;

    @NotBlank(message = "{user.blank.email}")
    @Email(message = "{user.valid.email}")
    private String email;

    @Pattern(regexp = "^(\\+91|0)?[6-9]\\d{9}$", message = "{user.valid.phoneNo}")
    private String phoneNumber;

    @NotBlank(message = "{contact.blank.address}")
    private String address;

    private String description;

    private boolean favorite;

    private String websiteLink;

    private String linkedInLink;

    @ValidFile(message = "{contact.valid.file}")
    private MultipartFile contactImage;

    private String picture;

}
