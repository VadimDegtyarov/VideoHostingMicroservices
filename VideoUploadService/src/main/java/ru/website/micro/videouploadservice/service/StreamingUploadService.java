package ru.website.micro.videouploadservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.website.micro.videouploadservice.dto.UploadProgressDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamingUploadService {
    private final MinioClient minioClient;
    private final Map<UUID, UploadProgress> activeUploads = new ConcurrentHashMap<>();

    @Value("${minio.bucket.videos.name}")
    private String bucketName;

    public UploadProgress startUpload(UUID userId, InputStream inputStream, long contentLength) {
        UUID uploadId = UUID.randomUUID();
        UploadProgress progress = new UploadProgress(
                uploadId,
                userId,
                contentLength,
                new AtomicLong(0),
                new AtomicLong(contentLength),
                false,
                null
        );

        activeUploads.put(uploadId, progress);

        CompletableFuture.runAsync(() -> {
            try (CountingInputStream countingStream = new CountingInputStream(inputStream, progress)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(uploadId.toString())
                                .stream(countingStream, -1, 10485760)
                                .build()
                );
                progress.setComplete(true);
            } catch (Exception e) {
                progress.setError(e.getMessage());
                log.error("Upload failed: {}", e.getMessage());
            } finally {
                activeUploads.remove(uploadId);
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    log.warn("Error closing stream: {}", ex.getMessage());
                }
            }
        });

        return progress;
    }

    public UploadProgress getProgress(UUID uploadId) {
        UploadProgress progress = activeUploads.get(uploadId);
        if (progress == null) {
            return UploadProgress.builder()
                    .uploadId(uploadId)
                    .error("Upload not found")
                    .build();
        }
        return progress;
    }


    private static class CountingInputStream extends InputStream {
        private final InputStream wrapped;
        private final UploadProgress progress;
        private long count;

        public CountingInputStream(InputStream in, UploadProgress progress) {
            this.wrapped = in;
            this.progress = progress;
        }

        @Override
        public int read() throws IOException {
            int b = wrapped.read();
            if (b != -1) {
                count++;
                progress.getBytesRead().set(count);
            }
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = wrapped.read(b, off, len);
            if (n != -1) {
                count += n;
                progress.getBytesRead().set(count);
            }
            return n;
        }

        @Override
        public void close() throws IOException {
            wrapped.close();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class UploadProgress {
        private final UUID uploadId;
        private final UUID userId;
        private final long totalBytes;
        private final AtomicLong bytesRead;
        private final AtomicLong lastUpdated;
        private boolean complete;
        private String error;

        public double getProgressPercentage() {
            return totalBytes > 0 ?
                    (bytesRead.get() * 100.0) / totalBytes : 0;
        }

        public UploadProgressDTO toDto() {
            return UploadProgressDTO.builder()
                    .uploadId(uploadId)
                    .userId(userId)
                    .bytesUploaded(bytesRead.get())
                    .totalBytes(totalBytes)
                    .progress(getProgressPercentage())
                    .status(complete ? "COMPLETED" :
                            error != null ? "FAILED" : "IN_PROGRESS")
                    .errorMessage(error)
                    .lastUpdated(System.currentTimeMillis())
                    .build();
        }
    }
}