package ru.website.micro.recommendationservice.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUserId implements Serializable {
    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "user_id")
    private UUID userId;

}
