package ru.website.micro.videouploadservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.website.micro.videouploadservice.exception.ImageUploadException;
import ru.website.micro.videouploadservice.prop.MinioProperties;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile image) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed" + e.getMessage());
        }
        if (image.isEmpty() || image.getOriginalFilename() == null) {
            throw new ImageUploadException("Image must have name.");
        }
        String fileName = generateFileName(image);
        InputStream inputStream;
        try {
            inputStream = image.getInputStream();
        } catch (Exception e) {
            throw new ImageUploadException("Image must have name.");
        }
        saveImage(inputStream, fileName);
        return fileName;
    }

    @SneakyThrows
    public InputStream  getImage(String fileName) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.getImagesBucketName())
                .object(fileName)
                .build());
    }

    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getImagesBucketName())
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getImagesBucketName())
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getImagesBucketName())
                .object(fileName)
                .build());
    }
}
