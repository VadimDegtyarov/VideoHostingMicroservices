package ru.website.micro.videouploadservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.videouploadservice.dto.ImportVideoDto;
import ru.website.micro.videouploadservice.exception.ResourceNotFoundException;
import ru.website.micro.videouploadservice.exception.VideoUploadException;
import ru.website.micro.videouploadservice.model.Tag;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.model.VideoQuality;
import ru.website.micro.videouploadservice.model.user.User;
import ru.website.micro.videouploadservice.prop.MinioProperties;
import ru.website.micro.videouploadservice.repository.TagRepository;
import ru.website.micro.videouploadservice.repository.VideoRepository;
import ru.website.micro.videouploadservice.repository.user.UserRepository;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    @Value("${video.upload.max-size}")
    private Long maxSizeFile;
    private final VideoFileService videoFileService;
    private final VideoRepository videoRepository;
    private final VideoProcessingService videoProcessingService;
    private final ImageFileService imageFileService;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final Tika tika;

    private User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с id:%s не найден".formatted(id)));
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Видео с id: %s не найдено".formatted(id)));
    }

    @Transactional
    public Video uploadVideo(UUID userId, ImportVideoDto videoDto) throws ExecutionException, InterruptedException {
        User author = getUserById(userId);

        Set<Tag> tags = new LinkedHashSet<>();

        if (videoDto.getTags() != null && !videoDto.getTags().isEmpty()) {
            tags = Arrays.stream(videoDto.getTags().split(","))
                    .map(String::trim)
                    .filter(tagName -> !tagName.isEmpty())
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build())))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        String videoUuid = UUID.randomUUID().toString();
        Map<String, String> qualities = videoProcessingService.processVideo(
                videoDto.getVideoFile(),
                videoUuid
        ).get();

        Video video = Video.builder()
                .videoName(videoDto.getVideoName())
                .author(author)
                .createdAt(LocalDate.now())
                .tags(tags)
                .build();

        video.getTags().addAll(tags);

        qualities.forEach((quality, url) ->
                video.getQualities().add(
                        VideoQuality.builder()
                                .quality(quality)
                                .fileUrl(url)
                                .video(video)
                                .build()
                )
        );
        log.info("6 step");
        Long size = videoDto.getVideoFile().getSize();
        if (size > maxSizeFile) {
            throw new VideoUploadException("File size exceeds the limit:%s".formatted(size));
        }
        String thumbnailFileName = imageFileService.upload(videoDto.getImage());
        log.info("7 step");

        video.setThumbnailUrl(thumbnailFileName);

        return videoRepository.save(video);
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

    @Cacheable(value = "videoMimeTypes", key = "{#id, #quality}")
    public String getMimeType(Long id, String quality) throws IOException {
        Video video = getVideoById(id);

        VideoQuality videoQuality = video.getQualities().stream()
                .filter(q -> q.getQuality().equals(quality))
                .findFirst()
                .orElseGet(() -> video.getQualities().stream()
                        .filter(q -> q.getQuality().equals("1080p"))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No video qualities available"))
                );

        try (InputStream stream = videoFileService.getVideo(videoQuality.getFileUrl())) {
            return tika.detect(stream);
        }
    }

    public ResponseEntity<InputStreamResource> getVideo(
            Long id,
            @RequestParam(defaultValue = "1080p") String quality) throws IOException {

        try {
            Video video = getVideoById(id);

            // Получаем URL файла для запрошенного качества
            String fileUrl = video.getQualities().stream()
                    .filter(q -> q.getQuality().equals(quality))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Quality not found"))
                    .getFileUrl();

            // Получаем поток и буферизуем его
            InputStream rawStream = videoFileService.getVideo(fileUrl);
            BufferedInputStream bufferedStream = new BufferedInputStream(rawStream);
            bufferedStream.mark(Integer.MAX_VALUE);

            // Определяем MIME-тип
            String mimeType = tika.detect(bufferedStream);
            bufferedStream.reset();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(new InputStreamResource(bufferedStream));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка получения видео",
                    e
            );
        }
    }

    public ResponseEntity<InputStreamResource> getThumbnail(Long id) {
        return getImage(id);
    }


    public List<String> getAvailableQualities(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Видео не найдено"))
                .getQualities()
                .stream()
                .map(VideoQuality::getQuality)
                .toList();
    }

    public Page<Video> getVideos(Boolean isChecked, Long lastVideoId, Pageable pageable) {
        return videoRepository.findFilteredVideos(isChecked, lastVideoId, pageable);
    }

    public Video updateChecked(Long videoId) {
        try {
            Video video = videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException("Видео не найдено"));
            video.setIsChecked(Boolean.TRUE);
            return videoRepository.save(video);
        } catch (VideoUploadException e) {
            throw new VideoUploadException("Не удалось обновить статус", e);
        }

    }

    public void deleteVideo(Long videoId) {
        try {
            videoRepository.deleteById(videoId);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Не удалось удалить видео", e);
        }
    }
}
