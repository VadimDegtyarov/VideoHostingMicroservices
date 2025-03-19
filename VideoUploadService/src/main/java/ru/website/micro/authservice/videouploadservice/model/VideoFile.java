package ru.website.micro.authservice.videouploadservice.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoFile {
    private String mimeType;
    private MultipartFile file;
}
