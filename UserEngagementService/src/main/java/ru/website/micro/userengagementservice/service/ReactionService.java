package ru.website.micro.userengagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.website.micro.userengagementservice.dto.ReactionDto;
import ru.website.micro.userengagementservice.enums.ReactionType;
import ru.website.micro.userengagementservice.enums.TargetType;
import ru.website.micro.userengagementservice.exception.DuplicateResourceException;
import ru.website.micro.userengagementservice.exception.ResourceNotFoundException;
import ru.website.micro.userengagementservice.mapper.ReactionMapper;
import ru.website.micro.userengagementservice.model.Comment;
import ru.website.micro.userengagementservice.model.Reaction;
import ru.website.micro.userengagementservice.model.ReactionId;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.model.user.User;
import ru.website.micro.userengagementservice.repository.ReactionRepository;
import ru.website.micro.userengagementservice.repository.user.UserRepository;

import javax.naming.ldap.PagedResultsControl;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final UserVideoHelper userVideoHelper;
    private final ReactionMapper reactionMapper;
    public ReactionDto createReaction(ReactionDto reactionDto) {
        UUID userId = reactionDto.getUserId();
        User user = userVideoHelper.getUserById(userId);
        ReactionId id = new ReactionId(
                userId,
                reactionDto.getTargetId()
        );
        if (reactionRepository.existsById(id)) {
            throw new DuplicateResourceException("Реакция уже существует");
        }
        Reaction reaction = Reaction.builder()
                .reactionId(id)
                .user(user)
                .reactionType(reactionDto.getReactionType())
                .build();
        Reaction savedReaction = reactionRepository.save(reaction);
        return reactionMapper.toDto(savedReaction);
    }
}
