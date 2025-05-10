package ru.website.micro.videouploadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadProgressDTO {
    private UUID uploadId;
    private UUID userId;
    private long bytesUploaded;
    private long totalBytes;
    private double progress;
    private String status;
    private String errorMessage;
    private long lastUpdated;

    public double getProgress() {
        return totalBytes > 0 ?
                (bytesUploaded * 100.0) / totalBytes : 0;
    }
}