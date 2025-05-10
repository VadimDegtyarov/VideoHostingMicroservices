package ru.website.micro.videouploadservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.website.micro.videouploadservice.dto.UploadProgressDTO;
import ru.website.micro.videouploadservice.dto.VideoDTO;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.service.StreamingUploadService;
import ru.website.micro.videouploadservice.service.VideoService;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController()
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final StreamingUploadService streamingUploadService;

    @PostMapping(value = "/{userId}/videos/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadProgressDTO> uploadVideoStream(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        StreamingUploadService.UploadProgress progress = streamingUploadService.startUpload(
                userId,
                file.getInputStream(),
                file.getSize()
        );

        return ResponseEntity.accepted().body(progress.toDto());
    }

    @GetMapping("/uploads/{uploadId}/progress")
    public ResponseEntity<UploadProgressDTO> getUploadProgress(
            @PathVariable UUID uploadId) {

        StreamingUploadService.UploadProgress progress = streamingUploadService.getProgress(uploadId);
        return ResponseEntity.ok(progress.toDto());
    }

    @GetMapping("/videos/{videoId}/thumbnail")
    public ResponseEntity<InputStreamResource> getThumbnail(@PathVariable Long videoId) {
        return videoService.getThumbnail(videoId);
    }

    @GetMapping("/videos")
    public ResponseEntity<Page<Video>> getVideos(@RequestParam Boolean isChecked,
                                                 @RequestParam Long lastVideoId,
                                                 @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(videoService.getVideos(isChecked, lastVideoId, pageable));
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<VideoDTO> getVideo(@PathVariable Long videoId) {
        Video video = videoService.getVideoById(videoId);//<-перенести в сервис
        return ResponseEntity.ok(VideoDTO.builder()
                .durationVideo(video.getDurationVideo())
                .videoName(video.getVideoName())
                .authorUsername(video.getAuthor().getUsername())
                .numOfViews(video.getNumOfViews())
                .createdAt(video.getCreatedAt())
                .userId(video.getAuthor().getId())
                .videoId(video.getId())

                .build());
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<HttpStatus> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{videoId}/is-valid")
    public ResponseEntity<Video> updateChecked(@PathVariable Long videoId) {
        return ResponseEntity.ok(videoService.updateChecked(videoId));
    }
}
