package ru.website.micro.recommendationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.website.micro.recommendationservice.model.Video;

import java.util.List;
import java.util.Set;
import java.util.UUID;
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByAuthorId(UUID id);

    /**
     * Возвращает случайные видео, которые пользователь ещё не смотрел и не пометил "не интересно".
     */
    @Query(value = """
            SELECT * FROM video v
            WHERE (:watchedIds IS NULL OR v.id NOT IN :watchedIds)
              AND (:notInterestedIds IS NULL OR v.id NOT IN :notInterestedIds)
            ORDER BY random()
            LIMIT :limit
            """, nativeQuery = true)
    List<Video> findRandomUnseen(@Param("watchedIds") Set<Long> watchedIds,
                                 @Param("notInterestedIds") Set<Long> notInterestedIds,
                                 @Param("limit") int limit);
    @Query(value = """
            SELECT * FROM video v
            ORDER BY random()
            LIMIT :limit
            """, nativeQuery = true)
    List<Video> findRandomUnseen(@Param("limit") int limit);

    @Query("SELECT v.durationVideo FROM Video v WHERE v.id = :vid")
    int getDuration(@Param("vid") Long vid);


    @Query("SELECT COALESCE(MAX(v.id), 0) FROM Video v")
    int findMaxId();
}
