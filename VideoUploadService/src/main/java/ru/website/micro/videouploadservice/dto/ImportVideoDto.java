package ru.website.micro.videouploadservice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ru.website.micro.videouploadservice.model.Image;
import ru.website.micro.videouploadservice.model.Tag;
import ru.website.micro.videouploadservice.model.VideoFile;
import ru.website.micro.videouploadservice.model.user.User;

import java.util.HashSet;
import java.util.Set;

@Data
public class ImportVideoDto {
    private String videoName;
    private MultipartFile image;
    private MultipartFile videoFile;
    private String tags;
    private String description;
}
