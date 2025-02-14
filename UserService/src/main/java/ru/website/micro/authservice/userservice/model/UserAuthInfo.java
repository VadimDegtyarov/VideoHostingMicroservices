package ru.website.micro.authservice.userservice.model;

import jakarta.persistence.*;

import lombok.*;

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

public class UserAuthInfo  {
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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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
