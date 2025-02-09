package ru.website.micro.authservice.authservice.model;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
@Getter
@Setter
@Table(name = "users_auth_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserAuthInfo implements UserDetails {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "password_hash")
    private String passwordHash;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
    }

    public String getPassword() {
        return passwordHash;
    }

    public String getUsername() {
        return email;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

}
