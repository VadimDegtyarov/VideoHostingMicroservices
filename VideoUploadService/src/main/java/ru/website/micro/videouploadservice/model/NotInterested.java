package ru.website.micro.videouploadservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.website.micro.videouploadservice.model.user.User;

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
