package ru.website.micro.videoprocessingservice.service;


import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.videoprocessingservice.dto.ImportVideoDto;
import ru.website.micro.videoprocessingservice.exception.ResourceNotFoundException;
import ru.website.micro.videoprocessingservice.exception.VideoUploadException;
import ru.website.micro.videoprocessingservice.model.Tag;
import ru.website.micro.videoprocessingservice.model.Video;
import ru.website.micro.videoprocessingservice.model.VideoQuality;
import ru.website.micro.videoprocessingservice.model.user.User;
import ru.website.micro.videoprocessingservice.repository.TagRepository;
import ru.website.micro.videoprocessingservice.repository.VideoRepository;
import ru.website.micro.videoprocessingservice.repository.user.UserRepository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {
    @Value("${video.upload.max-size}")
    private Long maxSizeFile;
    private final VideoFileService videoFileService;
    private final VideoRepository videoRepository;
    private final ImageFileService imageFileService;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final Tika tika;

    private User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с id:%s не найден".formatted(id)));
    }
    @Transactional(readOnly = true)
    public Video getVideoById(Long id) {
        return videoRepository.findByIdWithQualities(id).orElseThrow(
                () -> new ResourceNotFoundException("Видео с id: %s не найдено".formatted(id)));
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
        String fileUrl = video.getQualities().stream()
                .filter(q -> q.getQuality().equals(quality))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Quality not found"))
                .getFileUrl();

        try (InputStream rawStream = videoFileService.getVideo(fileUrl);
             BufferedInputStream bufferedStream = new BufferedInputStream(rawStream)) {

            byte[] header = new byte[4096];
            int readBytes = bufferedStream.read(header);

            if (readBytes == -1) {
                throw new IOException("Пустой файл");
            }

            return tika.detect(Arrays.copyOfRange(header, 0, readBytes), fileUrl);
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


}
