package ru.website.micro.videouploadservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.videouploadservice.model.Video;


public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("""
        SELECT v 
        FROM Video v 
        WHERE (:isChecked IS NULL OR v.isChecked = :isChecked)
        AND (:lastVideoId IS NULL OR v.id < :lastVideoId)
        ORDER BY v.createdAt DESC
    """)
    Page<Video> findFilteredVideos(
            @Param("isChecked") Boolean isChecked,
            @Param("lastVideoId") Long lastVideoId,
            Pageable pageable
    );
}