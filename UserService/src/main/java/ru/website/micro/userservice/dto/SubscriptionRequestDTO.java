package ru.website.micro.userservice.dto;

import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    private String loginCurrentUser;
    private String loginTargetUser;
}
