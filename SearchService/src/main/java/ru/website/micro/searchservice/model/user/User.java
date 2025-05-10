package ru.website.micro.searchservice.model.user;

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
@EqualsAndHashCode(exclude = "userAuthInfo")
public class User {
    @Id
    private UUID id;
    @Column(name = "username")
    private String username;
    @JsonBackReference
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserAuthInfo userAuthInfo;
    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="birth_date")
    private Instant birthDate;
    @Column(name = "avatar_url")
    private String avatarURL;
    @Column(name="num_of_subs")
    private Integer numOfSubs;

    //Подписки данного пользователя на другие каналы
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscription",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_subs_id")
    )
    Collection<User> subscription;
}
