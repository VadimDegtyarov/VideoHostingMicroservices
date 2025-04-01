package ru.website.micro.userservice.config;

import io.minio.MinioClient;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.website.micro.userservice.prop.MinioProperties;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final MinioProperties minioProperties;
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder().endpoint(minioProperties.getMinioURL())
                .credentials(minioProperties.getAccessName(), minioProperties.getAccessSecret())
                .build();
    }

}
