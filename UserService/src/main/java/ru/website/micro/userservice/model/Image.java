package ru.website.micro.userservice.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Image {
    private String mimeType;
    private MultipartFile file;
}
