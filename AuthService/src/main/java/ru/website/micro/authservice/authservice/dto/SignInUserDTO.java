package ru.website.micro.authservice.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInUserDTO {

    @Pattern(regexp = ".+@.+\\..+|\\+\\d{6,15}", message = "Логин должен быть адресом электронной почты или номером телефона")
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;
    @Size(min = 8, max = 255, message = "Слишном короткий пароль!")
    private String password;

}
