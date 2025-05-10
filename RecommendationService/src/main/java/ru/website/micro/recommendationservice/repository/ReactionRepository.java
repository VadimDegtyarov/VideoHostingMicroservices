package ru.website.micro.recommendationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.recommendationservice.enums.ReactionType;
import ru.website.micro.recommendationservice.enums.TargetType;
import ru.website.micro.recommendationservice.model.Video;
import ru.website.micro.recommendationservice.model.userInteractions.Reaction;
import ru.website.micro.recommendationservice.model.userInteractions.ReactionId;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
    Optional<Reaction> getReactionByReactionId(ReactionId reactionId);

    @Query("""
                SELECT COUNT(r)
                FROM Reaction r
                WHERE r.reactionId.targetId = :targetId
                  AND r.targetType = :targetType
                  AND r.reactionType = :reactionType
            """)
    Integer countByTargetIdAndTargetTypeAndReactionType(
            @Param("targetId") Long targetId,
            @Param("targetType") TargetType targetType,
            @Param("reactionType") ReactionType reactionType
    );

    @Query(value = """
                SELECT r.reactionId.targetId 
                FROM Reaction r 
                WHERE r.reactionId.userId = :userId 
                  AND r.targetType = :targetType 
                  AND r.reactionType = :reactionType
                  AND (:lastTargetId IS NULL OR r.reactionId.targetId < :lastTargetId)
                ORDER BY r.reactionId.targetId DESC
            """, countQuery = """
                SELECT COUNT(r) 
                FROM Reaction r 
                WHERE r.reactionId.userId = :userId 
                  AND r.targetType = :targetType 
                  AND r.reactionType = :reactionType
                  AND (:lastTargetId IS NULL OR r.reactionId.targetId < :lastTargetId)
            """)
    Page<Long> findTargetIdsByUserAndType(
            @Param("userId") UUID userId,
            @Param("targetType") TargetType targetType,
            @Param("reactionType") ReactionType reactionType,
            @Param("lastTargetId") Long lastTargetId,
            Pageable pageable
    );

    @Query("SELECT r.reactionId.targetId FROM Reaction r WHERE r.reactionId.userId = :userId AND r.targetType = :type AND r.reactionType = :reactionType")
    List<Long> findTargetIdsByUserAndType(@Param("userId") UUID userId,
                                          @Param("type") TargetType type,
                                          @Param("reactionType") ReactionType reactionType);

    @Query("""
            SELECT wh.video
            FROM WatchHistory wh
            WHERE wh.user.id = :userId
            AND (:lastVideoId IS NULL OR wh.video.id < :lastVideoId)
            ORDER BY wh.video.id DESC
            """)
    Page<Video> findWatchHistoryByUser(
            @Param("userId") UUID userId,
            @Param("lastVideoId") Long lastVideoId,
            Pageable pageable);

    @Query("SELECT r.reactionId.userId FROM Reaction r WHERE r.reactionId.targetId = :targetId AND r.targetType = :type AND r.reactionType = :reactionType")
    UUID findUserIdByTargetIdAndTargetType(@Param("targetId") Long targetId,
                                           @Param("type") TargetType type,
                                           @Param("reactionType") ReactionType reactionType);
}
