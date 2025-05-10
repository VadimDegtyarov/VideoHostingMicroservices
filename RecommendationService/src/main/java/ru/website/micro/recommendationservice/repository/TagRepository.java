package ru.website.micro.recommendationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.recommendationservice.model.Video;
import ru.website.micro.recommendationservice.model.userInteractions.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t JOIN t.videos v WHERE v.id = :videoId")
    List<Tag> findByVideoId(@Param("videoId") Long videoId);

    @Query("SELECT v FROM Video v JOIN v.tags t WHERE t.id = :tagId")
    List<Video> findByTag(@Param("tagId") Long tagId);

    @Query("SELECT DISTINCT t FROM Tag t JOIN t.videos v WHERE v.id IN :videoIds")
    List<Tag> findByVideoIds(@Param("videoIds") Set<Long> videoIds);

    @Query("SELECT DISTINCT v.id FROM Video v JOIN v.tags t WHERE t.id IN :tagIds")
    List<Long> findVideoIdsByTagIds(@Param("tagIds") Set<Long> tagIds);
}