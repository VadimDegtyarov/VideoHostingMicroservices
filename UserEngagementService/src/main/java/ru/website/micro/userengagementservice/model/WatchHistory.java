package ru.website.micro.userengagementservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.website.micro.userengagementservice.model.user.User;

import java.time.LocalDate;

@Data
@Entity
@Builder
@Table(name = "watch_history")
@NoArgsConstructor
@AllArgsConstructor
public class WatchHistory {
    @EmbeddedId
    private VideoUserId id;

    @CreationTimestamp
    @Column(name = "watched_at")
    private LocalDate watchedAt;
    @Column(name = "progress", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer progress = 0;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("videoId")
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
}
