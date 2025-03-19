package ru.website.micro.authservice.videouploadservice.model;

import lombok.Data;
import ru.website.micro.authservice.userservice.model.user.User;

@Data
@Entity
@Table(name = "time_video_watching")
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