package ru.website.micro.recommendationservice.model.userInteractions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.website.micro.recommendationservice.enums.ReactionType;
import ru.website.micro.recommendationservice.enums.TargetType;
import ru.website.micro.recommendationservice.model.user.User;


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
