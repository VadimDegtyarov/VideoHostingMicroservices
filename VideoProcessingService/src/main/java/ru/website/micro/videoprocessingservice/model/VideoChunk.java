package ru.website.micro.videoprocessingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoChunk {
    private String range;
    private byte[] data;
    private String contentType;
}