package ru.website.micro.userengagementservice.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.website.micro.userengagementservice.enums.TargetType;


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
