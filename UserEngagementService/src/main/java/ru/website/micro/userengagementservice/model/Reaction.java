package ru.website.micro.userengagementservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import ru.website.micro.userengagementservice.enums.ReactionType;
import ru.website.micro.userengagementservice.enums.TargetType;
import ru.website.micro.userengagementservice.model.user.User;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "reactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    @EmbeddedId
    private ReactionId reactionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 10, nullable = false)
    private TargetType targetType;



    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", length = 10, nullable = false)
    private ReactionType reactionType;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
