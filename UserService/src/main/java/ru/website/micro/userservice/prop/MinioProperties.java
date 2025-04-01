package ru.website.micro.userservice.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class MinioProperties {
    @Value("${minio.url}")
    private String minioURL;
    @Value("${minio.access.name}")
    private String accessName;
    @Value("${minio.access.secret}")
    private String accessSecret;
    @Value("${minio.bucket.videos.name}")
    private String videosBucketName;
    @Value("${minio.bucket.images.name}")
    private String imagesBucketName;
}
