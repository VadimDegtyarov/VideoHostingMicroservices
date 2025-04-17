package ru.website.micro.videouploadservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.website.micro.videouploadservice.exception.VideoUploadException;
import ru.website.micro.videouploadservice.prop.MinioProperties;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// VideoProcessingService.java
@Service
@Slf4j
@RequiredArgsConstructor
public class VideoProcessingService {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;
    private final Tika tika;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Async
    public Future<Map<String, String>> processVideo(MultipartFile originalFile, String baseUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("1 step");
                Path tempDir = Files.createTempDirectory("video_processing");
                File original = saveToTempFile(originalFile, tempDir);
                Map<String, String> qualities = Map.of(
                        "1080p", "scale=-2:1080",
                        "720p", "scale=-2:720",
                        "480p", "scale=-2:480"
                );
                log.info("2 step");
                Map<String, String> result = new HashMap<>();
                for (Map.Entry<String, String> entry : qualities.entrySet()) {
                    log.info("2.1 step");

                    File output = transcode(original, tempDir, entry.getValue(), entry.getKey());
                    log.info("2.2 step");

                    String url = uploadToMinio(output, baseUuid, entry.getKey());
                    log.info("2.3 step");

                    result.put(entry.getKey(), url);
                }
                log.info("3 step");

                FileUtils.deleteDirectory(tempDir.toFile());

                return result;
            } catch (Exception e) {
                throw new VideoUploadException("Video processing failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
            }
        });
    }

    private File transcode(File input, Path tempDir, String filter, String quality) throws Exception {
        log.info("Starting transcoding to {}", quality);

        File output = tempDir.resolve(quality + ".mp4").toFile();

        ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath,
                "-y",
                "-i", input.getAbsolutePath(),
                "-vf", filter,
                "-c:v", "libx264",
                "-preset", "fast",
                "-c:a", "aac",
                output.getAbsolutePath()
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        CompletableFuture<Void> outputReader = CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("[FFmpeg {}] {}", quality, line);
                }
            } catch (IOException e) {
                log.error("Error reading FFmpeg output", e);
            }
        });
        if (!process.waitFor(30, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            throw new RuntimeException("FFmpeg timeout exceeded");
        }

        outputReader.get(1, TimeUnit.MINUTES);

        if (process.exitValue() != 0) {
            throw new RuntimeException("FFmpeg failed with exit code: " + process.exitValue());
        }

        log.info("Successfully transcoded to {}", quality);
        return output;
    }

    @PostConstruct
    public void validateFFmpeg() {
        try {
            Process process = new ProcessBuilder(ffmpegPath, "-version").start();
            boolean success = process.waitFor(10, TimeUnit.SECONDS);
            if (!success || process.exitValue() != 0) {
                throw new IllegalStateException("FFmpeg validation failed");
            }
            log.info("FFmpeg initialized successfully");
        } catch (Exception e) {
            throw new VideoUploadException("FFmpeg initialization failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private String uploadToMinio(File file, String baseUuid, String quality) {
        try (InputStream is = new FileInputStream(file)) {
            log.info("minio 1 step");
            String fileName = baseUuid + "_" + quality + ".mp4";

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getVideosBucketName())
                            .object(fileName)
                            .stream(is, file.length(), -1)
                            .contentType("video/mp4")
                            .build());
            log.info("minio 2 step");

            return fileName;
        } catch (Exception e) {
            throw new VideoUploadException("Upload failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private File saveToTempFile(MultipartFile file, Path dir) throws IOException {
        File temp = dir.resolve("original").toFile();
        file.transferTo(temp);
        return temp;
    }
}