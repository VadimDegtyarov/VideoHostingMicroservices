package ru.website.micro.authservice.authservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "users_auth_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class UserAuthInfo implements UserDetails {
    @Id
    @Column(name = "user_id")
    private UUID id;
    @JsonManagedReference
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", unique = true)
    private User user;
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


    public Collection<GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
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
