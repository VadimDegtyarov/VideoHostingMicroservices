package ru.website.micro.authservice.authservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.website.micro.authservice.authservice.model.Role;

import java.util.Collection;

@Data
@Builder
public class SignUpUserDTO {
    @Size(min = 5, max = 255, message = "Адресс электронной почты должен содержать минимум 5 символов")
    @Email(message = "Адрес электронной почты должен быть в формате user@ex.com")
    @NotBlank(message = "Адресс электронной почты не должен быть пустным")
    private String email;
    @Size(min = 6, max = 15, message = "Неверный номер телефона")
    private String phoneNumber;
    @Size(min = 8, max = 255, message = "Слишном короткий пароль!")
    private String password;
}
