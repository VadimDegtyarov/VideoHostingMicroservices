package ru.website.micro.recommendationservice.model.userInteractions;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionId {
    private UUID userId;
    private Long targetId;
}
