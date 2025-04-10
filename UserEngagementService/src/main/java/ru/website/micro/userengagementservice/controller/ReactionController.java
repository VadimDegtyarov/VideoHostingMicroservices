package ru.website.micro.userengagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userengagementservice.dto.ReactionDto;
import ru.website.micro.userengagementservice.enums.ReactionType;
import ru.website.micro.userengagementservice.enums.TargetType;
import ru.website.micro.userengagementservice.service.ReactionService;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController()
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("user/{userId}/target/{targetId}")
    public ResponseEntity<ReactionDto> createOrUpdateReaction(
            @PathVariable UUID userId,
            @PathVariable Long targetId,
            @ModelAttribute ReactionDto reactionDto) {
        reactionDto.setTargetId(targetId);
        reactionDto.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reactionService.createOrUpdateReaction(reactionDto));
    }

    @GetMapping("user/{userId}/target/{targetId}")
    public ResponseEntity<ReactionDto> getReaction(
            @PathVariable UUID userId,
            @PathVariable Long targetId) {

        return ResponseEntity.ok(reactionService.getReaction(userId, targetId));
    }

    @GetMapping("/target/{targetId}/count-reaction")
    public ResponseEntity<Integer> countReactionOfTarget(
            @PathVariable Long targetId,
            @RequestParam TargetType targetType,
            @RequestParam ReactionType reactionType
    ) {
        return ResponseEntity.ok(
                reactionService.getCountReactionOfTarget(targetId, targetType, reactionType)
        );
    }

    @GetMapping("/user/{userId}/user-targets-with-reaction")
    public ResponseEntity<Page<?>> getUserTargetsWithReactions(
            @PathVariable UUID userId,
            @RequestParam TargetType targetType,
            @RequestParam ReactionType reactionType,
            @RequestParam(required = false) Long lastTargetId,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                reactionService.getUserTargetsWithReactions(
                        userId, targetType, reactionType, lastTargetId, pageable
                )
        );
    }

    @DeleteMapping("/user/{userId}/target/{targetId}")
    public ResponseEntity<Void> deleteReaction(
            @PathVariable UUID userId,
            @PathVariable Long targetId) {
        reactionService.deleteReaction(userId, targetId);
        return ResponseEntity.noContent().build();
    }


}
