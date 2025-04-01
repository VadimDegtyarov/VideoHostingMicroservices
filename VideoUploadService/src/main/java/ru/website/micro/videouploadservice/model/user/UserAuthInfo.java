package ru.website.micro.videouploadservice.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


import java.util.Collection;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Table(name = "users_auth_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserAuthInfo {
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
