package ru.website.micro.authservice.authservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "userAuthInfo")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "username")
    private String username;
    @JsonBackReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserAuthInfo userAuthInfo;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birth_date")
    private Instant birthDate;
    @Column(name = "avatar_url")
    private String avatarURL;

    //Подписки данного пользователя на другие каналы
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_subs_id")
    )
    Collection<User> subscription;
}
