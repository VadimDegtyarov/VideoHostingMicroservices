package ru.website.micro.userengagementservice.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.website.micro.userengagementservice.model.user.User;


@Entity
@Data
@Table(name = "watch_later")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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