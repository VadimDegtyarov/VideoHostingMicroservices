package ru.website.micro.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AddInfoUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private Instant birthDate;
    private String avatarURL;
}
