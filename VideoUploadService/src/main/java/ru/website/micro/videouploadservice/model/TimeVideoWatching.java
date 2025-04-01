package ru.website.micro.videouploadservice.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.website.micro.videouploadservice.model.user.User;

@Data
@Entity
@Table(name = "time_video_watching")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeVideoWatching {
    @EmbeddedId
    private VideoUserId id;
    @Column(nullable = false)
    private Integer duration;

    @MapsId("videoId")
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}