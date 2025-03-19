package ru.website.micro.authservice.userservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.website.micro.authservice.userservice.Exception.VideoUploadException;
import ru.website.micro.authservice.userservice.model.video.VideoFile;
import ru.website.micro.authservice.userservice.prop.MinioProperties;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadVideo(VideoFile videoFile) {
        try {
            createVideoBucket();
        } catch (Exception e) {
            throw new VideoUploadException("Video upload failed: " + e.getMessage());
        }

        MultipartFile file = videoFile.getFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new VideoUploadException("Video file must have a name");
        }

        String fileName = generateVideoFileName(file);
        try (InputStream inputStream = file.getInputStream()) {
            saveVideo(inputStream, fileName);
            return fileName;
        } catch (Exception e) {
            throw new VideoUploadException("Error processing video file: " + e.getMessage());
        }
    }

    @SneakyThrows
    public InputStream getVideo(String fileName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getVideosBucketName())
                        .object(fileName)
                        .build());
    }

    @SneakyThrows
    private void createVideoBucket() {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(minioProperties.getVideosBucketName())
                        .build());

        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(minioProperties.getVideosBucketName())
                            .build());
        }
    }

    private String generateVideoFileName(MultipartFile file) {
        String extension = getVideoFileExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getVideoFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new VideoUploadException("Video file must have a valid name");
        }
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new VideoUploadException("Video file must have an extension");
        }
        return originalFilename.substring(lastDotIndex + 1);
    }

    @SneakyThrows
    private void saveVideo(InputStream inputStream, String fileName) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .bucket(minioProperties.getVideosBucketName())
                        .object(fileName)
                        .contentType(getContentType(fileName))
                        .build());
    }

    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return switch (extension.toLowerCase()) {
            case "mp4" -> "video/mp4";
            case "mov" -> "video/quicktime";
            case "avi" -> "video/x-msvideo";
            default -> "application/octet-stream";
        };
    }
}