package ru.website.micro.videouploadservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
public class VideoUserId implements Serializable {
    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "user_id")
    private UUID userId;

}
