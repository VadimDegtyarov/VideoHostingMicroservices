package ru.website.micro.authservice.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;
    @Column(name = "username")
    private String username;
    @OneToOne(mappedBy = "user")
    private UserAuthInfo userAuthInfo;
    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="birth_date")
    private Instant birthDate;
    @Column(name = "avatar_url")
    private String avatarURL;
    //Подписчики данного пользователя
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscribers",
            joinColumns = @JoinColumn(name = "user_sub_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Collection<User> subscribers;
    //Подписки данного пользователя на другие каналы
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_subs_id")
    )
    Collection<User> subscription;
}
