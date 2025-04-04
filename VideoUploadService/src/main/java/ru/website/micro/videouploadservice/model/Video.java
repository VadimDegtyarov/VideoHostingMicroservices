package ru.website.micro.videouploadservice.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.website.micro.videouploadservice.model.user.User;
import ru.website.micro.videouploadservice.model.Tag;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="video")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_name", nullable = false)
    private String videoName;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @Column(name = "num_of_likes", columnDefinition = "integer default 0")
    private Integer numOfLikes = 0;

    @Column(name = "num_of_dislikes", columnDefinition = "integer default 0")
    private Integer numOfDislikes = 0;

    @Column(name = "video_file_url", nullable = false)
    private String videoFileUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "total_time_watching")
    private Integer totalTimeWatching = 0;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany
    @JoinTable(
            name = "video_tags",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
