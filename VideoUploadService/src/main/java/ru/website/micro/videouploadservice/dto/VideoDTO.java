package ru.website.micro.videouploadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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