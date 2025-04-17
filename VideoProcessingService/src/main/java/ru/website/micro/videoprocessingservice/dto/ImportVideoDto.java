package ru.website.micro.videoprocessingservice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportVideoDto {
    private String videoName;
    private MultipartFile image;
    private MultipartFile videoFile;
    private String tags;
}
