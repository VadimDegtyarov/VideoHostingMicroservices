package ru.website.micro.videouploadservice.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.website.micro.videouploadservice.prop.MinioProperties;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final MinioProperties minioProperties;
    @Bean
    public Tika tika() {
        return new Tika();
    }
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder().endpoint(minioProperties.getMinioURL())
                .credentials(minioProperties.getAccessName(), minioProperties.getAccessSecret())
                .build();
    }


}
