package ru.website.micro.authservice.videouploadservice.model;

import lombok.Data;
import ru.website.micro.authservice.userservice.model.user.User;

@Entity
@Data
@Table(name = "watch_later")
public class WatchLater {
    @EmbeddedId
    private VideoUserId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("videoId")
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

}