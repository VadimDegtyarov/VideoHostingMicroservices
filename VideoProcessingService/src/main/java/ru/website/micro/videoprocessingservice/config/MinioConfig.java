package ru.website.micro.videoprocessingservice.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.website.micro.videoprocessingservice.prop.MinioProperties;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties minioProperties;
    @Bean
    public Tika tika() {
        return new Tika();
    }


    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getMinioURL())
                .credentials(minioProperties.getAccessName(), minioProperties.getAccessSecret())
                .build();
    }
}