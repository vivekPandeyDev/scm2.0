package com.scm.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRequest {
    @NotBlank(message = "{user.blank.name}")
    private String name;
    @Email(message = "{user.valid.email}")
    @NotBlank(message = "{user.blank.email}")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "{user.valid.password}")
    private String password;
    @Pattern(regexp = "^(\\+91|0)?[6-9]\\d{9}$", message = "{user.valid.phoneNo}")
    private String phoneNumber;
    @Size(min = 10,max = 20,message = "{user.size.about}")
    private String about;
}
