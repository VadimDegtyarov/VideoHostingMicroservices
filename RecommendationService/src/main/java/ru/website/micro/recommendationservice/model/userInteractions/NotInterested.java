package ru.website.micro.recommendationservice.model.userInteractions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.website.micro.recommendationservice.model.Video;
import ru.website.micro.recommendationservice.model.user.User;
import ru.website.micro.recommendationservice.model.user.VideoUserId;



@Data
@Entity(name = "not_interested")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotInterested {
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
