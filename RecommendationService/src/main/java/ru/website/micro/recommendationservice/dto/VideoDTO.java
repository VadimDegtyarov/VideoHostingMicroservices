package ru.website.micro.recommendationservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    Long videoId;
    String videoName;
    String authorUsername;
    UUID userId;
    Integer durationVideo;
    Integer numOfViews;
    LocalDate createdAt;
}