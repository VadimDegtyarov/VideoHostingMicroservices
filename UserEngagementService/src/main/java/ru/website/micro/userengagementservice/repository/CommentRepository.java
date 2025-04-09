package ru.website.micro.userengagementservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.userengagementservice.model.Comment;
import ru.website.micro.userengagementservice.model.Video;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByUser_IdAndVideo_Id(UUID userId, Long videoId);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.video.id = :videoId
            AND (:lastCommentId IS NULL OR c.id < :lastCommentId)
            ORDER BY c.id DESC
            """)
    Page<Comment> getCommentsByVideoId(
            @Param("videoId") Long videoId,
            @Param("lastCommentId") Long lastCommentId,
            Pageable pageable);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.user.id = :userId
            AND (:lastCommentId IS NULL OR c.id < :lastCommentId)
            ORDER BY c.id DESC
            """)
    Page<Comment> getCommentsByUserId(
            @Param("userId") Long userId,
            @Param("lastCommentId") Long lastCommentId,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Comment c SET c.text = :text WHERE c.id = :commentId AND c.user.id = :userId")
    int updateCommentText(@Param("userId") UUID userId,
                          @Param("commentId") Long commentId,
                          @Param("text") String text);
}
