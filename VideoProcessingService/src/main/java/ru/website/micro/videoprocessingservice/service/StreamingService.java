package ru.website.micro.videoprocessingservice.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.website.micro.videoprocessingservice.enums.AllowedVideoFormats;
import ru.website.micro.videoprocessingservice.exception.ResourceNotFoundException;
import ru.website.micro.videoprocessingservice.model.Video;
import ru.website.micro.videoprocessingservice.model.VideoChunk;
import ru.website.micro.videoprocessingservice.model.VideoQuality;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingService {
    private final MinioClient minioClient;
    private final VideoService videoService;
    private final VideoFileService videoFileService;
    @Value("${minio.bucket.videos.name}")
    private String BUCKET_NAME;
    private static final long CHUNK_SIZE = 1024 * 1024;
    @Transactional(readOnly = true)
    public Mono<VideoChunk> getVideoChunk(Long videoId, String quality, String rangeHeader) {
        log.info("1 step");
        return Mono.fromCallable(() -> {
            try {
                log.info("Request for video {} quality {} range {}", videoId, quality, rangeHeader);

                Video video = videoService.getVideoById(videoId);
                VideoQuality videoQuality = video.getQualities().stream()
                        .filter(q -> q.getQuality().equals(quality))
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error("Quality {} not found for video {}", quality, videoId);
                            return new ResourceNotFoundException("Quality not found");
                        });

                String fileURL = videoQuality.getFileUrl();
                log.debug("Processing file: {}", fileURL);

                StatObjectResponse stat = minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(fileURL)
                                .build());
                long totalLength = stat.size();

                String mimeType = determineContentType(fileURL, stat.contentType());


                long start = 0;
                long end = CHUNK_SIZE - 1;

                if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                    String[] ranges = rangeHeader.substring(6).split("-");
                    start = Long.parseLong(ranges[0]);
                    end = ranges.length > 1
                            ? Long.parseLong(ranges[1])
                            : Math.min(start + CHUNK_SIZE - 1, totalLength - 1);
                }

                end = Math.min(end, totalLength - 1);
                if (start > end) throw new IllegalArgumentException("Invalid range");

                try (InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(fileURL)
                                .offset(start)
                                .length(end - start + 1)
                                .build())) {

                    byte[] data = stream.readAllBytes();
                    return new VideoChunk(
                            "bytes " + start + "-" + end + "/" + totalLength,
                            data,
                            mimeType
                    );
                }
            } catch (MinioException e) {
                throw new RuntimeException("Minio error: " + e.getMessage());
            }
        }).onErrorResume(e -> Mono.error(new ResponseStatusException(
                e instanceof IllegalArgumentException
                        ? HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE
                        : HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage()
        )));
    }

    private String determineContentType(String objectName, String minioContentType) {
        // 1. Пробуем получить из расширения файла
        String extension = objectName.substring(objectName.lastIndexOf('.') + 1);
        Optional<AllowedVideoFormats> format = Arrays.stream(AllowedVideoFormats.values())
                .filter(f -> f.getExtension().equalsIgnoreCase(extension))
                .findFirst();

        if (format.isPresent()) {
            return format.get().getMimeType();
        }

        // 2. Используем тип из метаданных Minio
        if (AllowedVideoFormats.getAllVideoFormats().contains(minioContentType)) {
            return minioContentType;
        }

        // 3. Дефолтное значение
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}