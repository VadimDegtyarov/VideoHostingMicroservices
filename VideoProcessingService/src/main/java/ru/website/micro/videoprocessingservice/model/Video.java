package ru.website.micro.videoprocessingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.website.micro.videoprocessingservice.model.user.User;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @OneToMany(
            mappedBy = "video",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(FetchMode.JOIN)
    private List<VideoQuality> qualities = new ArrayList<>();


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
