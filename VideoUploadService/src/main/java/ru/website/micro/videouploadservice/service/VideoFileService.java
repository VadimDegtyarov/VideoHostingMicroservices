package ru.website.micro.videouploadservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.website.micro.videouploadservice.enums.AllowedVideoFormats;
import ru.website.micro.videouploadservice.exception.VideoUploadException;
import ru.website.micro.videouploadservice.prop.MinioProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoFileService {

    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024 * 1024 * 1024; // 2GB

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final Tika tika;

    public String upload(MultipartFile videoFile) {
        try {
            // 1. Проверка размера файла
            validateFileSize(videoFile);

            // 2. Валидация MIME-типа
            String detectedType = validateContentType(videoFile);

            // 3. Создание бакета при необходимости
            createVideoBucket();

            // 4. Генерация безопасного имени файла
            String extension = getFileExtension(detectedType);
            String fileName = generateVideoFileName(extension);

            // 5. Сохранение файла
            try (InputStream inputStream = videoFile.getInputStream()) {
                saveVideo(
                        inputStream,
                        fileName,
                        detectedType,
                        videoFile.getSize()
                );
            }

            return fileName;

        } catch (IOException e) {
            throw new VideoUploadException("Video processing failed", HttpStatus.BAD_REQUEST, e);
        }
    }

    public InputStream getVideo(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getVideosBucketName())
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new VideoUploadException("File not found", HttpStatus.NOT_FOUND, e);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new VideoUploadException(
                    "File size exceeds limit",
                    HttpStatus.PAYLOAD_TOO_LARGE
            );
        }
    }

    private String validateContentType(MultipartFile videoFile) throws IOException {
        String headerType = videoFile.getContentType();
        if (headerType == null) {
            throw new VideoUploadException(
                    "Missing Content-Type header",
                    HttpStatus.BAD_REQUEST
            );
        }


        try (InputStream stream = videoFile.getInputStream()) {
            String realType = tika.detect(stream);

            if (realType.equals("application/x-matroska")) {
                realType = "video/x-matroska";
            }
            if (!realType.equals(headerType)) {
                throw new VideoUploadException(
                        "Content-Type mismatch. Declared: %s, Actual: %s"
                                .formatted(headerType, realType),
                        HttpStatus.BAD_REQUEST
                );
            }
            if (!AllowedVideoFormats.getAllVideoFormats().contains(realType)) {
                throw new VideoUploadException(
                        "Unsupported file type: " + realType,
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE
                );
            }

            return realType;
        }
    }

    private void createVideoBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioProperties.getVideosBucketName())
                            .build());

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioProperties.getVideosBucketName())
                                .build());
            }
        } catch (Exception e) {
            throw new VideoUploadException(
                    "Bucket initialization failed",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            );
        }
    }

    private String generateVideoFileName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    private String getFileExtension(String mimeType) {
        return AllowedVideoFormats.getExtension(mimeType)
                .orElseThrow(() -> new VideoUploadException(
                        "Unsupported MIME type",
                        HttpStatus.BAD_REQUEST
                ));
    }

    private void saveVideo(
            InputStream inputStream,
            String fileName,
            String contentType,
            long size
    ) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .stream(inputStream, size, -1)
                            .bucket(minioProperties.getVideosBucketName())
                            .object(fileName)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            throw new VideoUploadException(
                    "File upload failed",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            );
        }
    }
}