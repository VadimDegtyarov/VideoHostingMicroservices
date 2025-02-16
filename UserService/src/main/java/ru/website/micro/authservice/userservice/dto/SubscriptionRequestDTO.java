package ru.website.micro.authservice.userservice.dto;

import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    private String loginCurrentUser;
    private String loginTargetUser;
}
