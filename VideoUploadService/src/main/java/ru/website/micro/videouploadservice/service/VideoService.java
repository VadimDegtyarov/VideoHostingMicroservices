package ru.website.micro.videouploadservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.website.micro.videouploadservice.dto.ImportVideoDto;
import ru.website.micro.videouploadservice.exception.ResourceNotFoundException;
import ru.website.micro.videouploadservice.exception.VideoUploadException;
import ru.website.micro.videouploadservice.model.Tag;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.model.user.User;
import ru.website.micro.videouploadservice.prop.MinioProperties;
import ru.website.micro.videouploadservice.repository.TagRepository;
import ru.website.micro.videouploadservice.repository.VideoRepository;
import ru.website.micro.videouploadservice.repository.user.UserRepository;


import java.io.InputStream;
import java.net.URLConnection;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {
    @Value("${video.upload.max-size}")
    private Long maxSizeFile;
    private final VideoFileService videoFileService;
    private final VideoRepository videoRepository;
    private final MinioProperties minioProperties;
    private final ImageFileService imageFileService;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с id:%s не найден".formatted(id)));
    }

    private Video getVideoById(Long id) {
        return videoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Видео с id: %s не найдено".formatted(id)));
    }

    @Transactional
    public void uploadVideo(UUID userId, ImportVideoDto videoDto) {
        Set<Tag> tags = new HashSet<>();
        if (videoDto.getTags() != null && !videoDto.getTags().isEmpty()) {
            tags = Arrays.stream(videoDto.getTags().split(","))
                    .map(String::trim)
                    .filter(tagName -> !tagName.isEmpty())
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build())))
                    .collect(Collectors.toSet());
        }

        // Создание сущности Video
        Video video = Video.builder()
                .videoName(videoDto.getVideoName())
                .author(getUserById(userId))
                .createdAt(LocalDate.now())
                .tags(tags)
                .build();

        // Загрузка файлов
        Long size = videoDto.getVideoFile().getSize();
        if (size > maxSizeFile) {
            throw new VideoUploadException("File size exceeds the limit:%s".formatted(size));
        }
        String videoFileName = videoFileService.upload(videoDto.getVideoFile());
        String thumbnailFileName = imageFileService.upload(videoDto.getImage());

        video.setVideoFileUrl(videoFileName);
        video.setThumbnailUrl(thumbnailFileName);

        videoRepository.save(video);
    }


    private ResponseEntity<InputStreamResource> getImage(Long id) {
        Video video = getVideoById(id);
        String thumbnailUrl = video.getThumbnailUrl();
        InputStream inputStream = imageFileService.getImage(thumbnailUrl);
        String mimeType = URLConnection.guessContentTypeFromName(thumbnailUrl);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new InputStreamResource(inputStream));
    }

    public ResponseEntity<InputStreamResource> getVideo(Long id) {
        Video video = getVideoById(id);
        String videoFileName = video.getVideoFileUrl();
        InputStream videoStream = videoFileService.getVideo(videoFileName);
        String mimeType = URLConnection.guessContentTypeFromName(videoFileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new InputStreamResource(videoStream));
    }

    public ResponseEntity<InputStreamResource> getThumbnail(Long id) {
        return getImage(id);
    }


}
