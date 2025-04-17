package ru.website.micro.videouploadservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.videouploadservice.dto.ImportVideoDto;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.service.VideoService;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController()
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/{userId}/videos")
    public ResponseEntity<HttpStatus> uploadVideo(@PathVariable UUID userId, @ModelAttribute ImportVideoDto VideoDto) throws ExecutionException, InterruptedException {
        log.info(VideoDto.toString());
        return ResponseEntity.ok(videoService.uploadVideo(userId, VideoDto) != null ? HttpStatus.CREATED : HttpStatus.FORBIDDEN);
    }

    @GetMapping("/videos/{videoId}/thumbnail")
    public ResponseEntity<InputStreamResource> getThumbnail( @PathVariable Long videoId) {
        return videoService.getThumbnail(videoId);
    }

    @GetMapping()
    public ResponseEntity<Page<Video>> getVideos(@RequestParam Boolean isChecked,
                                                 @RequestParam Long lastVideoId,
                                                 @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(videoService.getVideos(isChecked, lastVideoId, pageable));
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
