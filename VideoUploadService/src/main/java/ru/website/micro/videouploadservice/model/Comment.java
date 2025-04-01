package ru.website.micro.videouploadservice.model;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.website.micro.videouploadservice.model.user.User;

import java.time.LocalDate;
@Deprecated
@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String text;

    @Column(name = "num_of_likes", columnDefinition = "integer default 0")
    private Integer numOfLikes = 0;

    @Column(name = "num_of_dislikes", columnDefinition = "integer default 0")
    private Integer numOfDislikes = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

}