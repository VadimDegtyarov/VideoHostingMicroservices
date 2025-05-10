package ru.website.micro.recommendationservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.website.micro.recommendationservice.model.user.User;
import ru.website.micro.recommendationservice.model.userInteractions.Tag;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="video")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "total_time_watching")
    private Integer totalTimeWatching = 0;
    @Column(name="is_checked",nullable = false,columnDefinition = "boolean default false")
    private Boolean isChecked;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User author;

    @ManyToMany
    @JoinTable(
            name = "video_tags",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
    @Column(name = "description")
    private String description;
    @Column(name = "duration_video")
    private Integer durationVideo;
    @Column(name="num_of_views")
    private Integer numOfViews;
}
