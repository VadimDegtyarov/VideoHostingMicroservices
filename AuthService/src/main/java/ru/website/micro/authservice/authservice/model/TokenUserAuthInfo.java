package ru.website.micro.authservice.authservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;
@Getter
@Setter
public class TokenUserAuthInfo extends UserAuthInfo {
    private final String token;

    public TokenUserAuthInfo(UUID id, String email, String phoneNumber, String passwordHash, Collection<Role> roles, String token,User user) {
        super(id,user, email, phoneNumber, passwordHash, roles);
        this.token = token;
    }

}
