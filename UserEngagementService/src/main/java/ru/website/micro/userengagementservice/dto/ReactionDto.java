package ru.website.micro.userengagementservice.dto;

import lombok.Data;
import ru.website.micro.userengagementservice.enums.ReactionType;
import ru.website.micro.userengagementservice.enums.TargetType;

import java.time.Instant;
import java.util.UUID;

@Data
public class ReactionDto {

    private UUID userId;
    private Long targetId;
    private TargetType targetType;
    private ReactionType reactionType;
    private Instant createdAt;

}
