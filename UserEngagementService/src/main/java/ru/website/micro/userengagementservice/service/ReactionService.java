package ru.website.micro.userengagementservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.website.micro.userengagementservice.dto.ReactionDto;
import ru.website.micro.userengagementservice.enums.ReactionType;
import ru.website.micro.userengagementservice.enums.TargetType;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.exception.UnsupportedTargetTypeException;
import ru.website.micro.userengagementservice.mapper.ReactionMapper;
import ru.website.micro.userengagementservice.model.Comment;
import ru.website.micro.userengagementservice.model.Reaction;
import ru.website.micro.userengagementservice.model.ReactionId;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.user.User;
import ru.website.micro.userengagementservice.repository.CommentRepository;
import ru.website.micro.userengagementservice.repository.ReactionRepository;
import ru.website.micro.userengagementservice.repository.VideoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final VideoRepository videoRepository;
    private final UserVideoHelper userVideoHelper;
    private final ReactionMapper reactionMapper;
    private final CommentRepository commentRepository;

    private void validateReactionDto(ReactionDto reactionDto) {
        if (reactionDto.getUserId() == null || reactionDto.getTargetId() == null) {
            throw new RuntimeException("Не заполнены обязательные данные");
        }
    }

    @Transactional
    public ReactionDto createOrUpdateReaction(ReactionDto reactionDto) {
        validateReactionDto(reactionDto);
        ReactionId id = new ReactionId(
                reactionDto.getUserId(),
                reactionDto.getTargetId()
        );
        Optional<Reaction> existingReaction = reactionRepository.findById(id);
        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getReactionType() == reactionDto.getReactionType()) {
                reactionRepository.delete(reaction);
                return null;
            } else {
                reaction.setReactionType(reactionDto.getReactionType());
                return reactionMapper.toDto(reactionRepository.save(reaction));
            }
        } else {
            User user = userVideoHelper.getUserById(reactionDto.getUserId());
            Reaction reaction = Reaction.builder()
                    .reactionId(id)
                    .user(user)
                    .reactionType(reactionDto.getReactionType())
                    .build();

            return reactionMapper.toDto(reactionRepository.save(reaction));
        }

    }

    public ReactionDto getReaction(UUID userId, Long targetId) {
        ReactionId reactionId = new ReactionId(userId, targetId);
        Reaction reaction = reactionRepository.getReactionByReactionId(reactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Реакции у пользователя с id %s нет реакции под целью %s".formatted(userId, targetId)));
        return reactionMapper.toDto(reaction);
    }

    @Cacheable(value = "reactionCounts", key = "{#targetId, #targetType, #reactionType}")
    public Integer getCountReactionOfTarget(Long targetId, TargetType targetType, ReactionType reactionType) {
        return reactionRepository.countByTargetIdAndTargetTypeAndReactionType(targetId, targetType, reactionType);
    }

    public Page<?> getUserTargetsWithReactions(
            UUID userId,
            TargetType targetType,
            ReactionType reactionType,
            Long lastTargetId,
            Pageable pageable
    ) {
        Page<Long> targetIdsPage = reactionRepository.findTargetIdsByUserAndType(
                userId,
                targetType,
                reactionType,
                lastTargetId,
                pageable
        );

        if (targetType == TargetType.VIDEO) {
            List<Video> videos = videoRepository.findAllById(targetIdsPage.getContent());
            return new PageImpl<>(videos, pageable, targetIdsPage.getTotalElements());
        } else if (targetType == TargetType.COMMENT) {
            List<Comment> comments = commentRepository.findAllById(targetIdsPage.getContent());
            return new PageImpl<>(comments, pageable, targetIdsPage.getTotalElements());
        }
        throw new UnsupportedTargetTypeException(targetType.toString());
    }

    public void deleteReaction(UUID userId, Long targetId) {
        ReactionId reactionId = new ReactionId(userId, targetId);
        Reaction reaction = reactionRepository.getReactionByReactionId(reactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Реакции у пользователя с id %s нет реакции под целью %s".formatted(userId, targetId)));
        reactionRepository.delete(reaction);
    }

}
