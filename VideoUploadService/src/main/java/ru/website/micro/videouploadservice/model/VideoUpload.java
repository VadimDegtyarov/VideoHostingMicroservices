package ru.website.micro.videouploadservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.website.micro.videouploadservice.enums.UploadStatus;
import ru.website.micro.videouploadservice.model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "video_uploads")
@Getter
@Setter
public class VideoUpload {
    @Id
    private UUID uploadId;

    @ManyToOne
    private User user;

    private long bytesUploaded;
    private long totalBytes;
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private UploadStatus status;

    private String errorMessage;
}

