package ru.website.micro.videoprocessingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.videoprocessingservice.model.Video;

import java.util.Optional;


public interface VideoRepository extends JpaRepository<Video,Long> {
    @Query("SELECT v FROM Video v LEFT JOIN FETCH v.qualities WHERE v.id = :id")
    Optional<Video> findByIdWithQualities(@Param("id") Long id);
}
