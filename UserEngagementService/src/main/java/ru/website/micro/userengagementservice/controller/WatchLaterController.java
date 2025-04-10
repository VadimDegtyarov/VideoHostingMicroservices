package ru.website.micro.userengagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.service.WatchLaterService;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/v1/user/{userId}/")
@RequiredArgsConstructor
public class WatchLaterController {
    private final WatchLaterService watchLaterService;

    @PostMapping("/{videoId}/watch-later")
    public ResponseEntity<HttpStatus> addToWatchLater(@PathVariable UUID userId, @PathVariable Long videoId) {
        return ResponseEntity.status(watchLaterService.addingWatchLater(userId, videoId) == null ? HttpStatus.NOT_FOUND : HttpStatus.CREATED).build();
    }

    @GetMapping("/get-watch-later")
    public ResponseEntity<Page<Video>> getWatchLaterVideos(@PathVariable UUID userId, @RequestParam(required = false) Long lastVideoId, @PageableDefault(size = 20, sort = "addedAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(watchLaterService.getWatchLaterVideos(userId, pageable, lastVideoId));
    }

    @GetMapping("/video/{videoId}/watch-later")
    public ResponseEntity<Boolean> isVideoInWatchLater(@PathVariable UUID userId, @PathVariable Long videoId) {
        return ResponseEntity.ok(watchLaterService.isVideoInWatchLater(userId, videoId));
    }
    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<Void> removeFromWatchLater(
            @PathVariable UUID userId,
            @PathVariable Long videoId
    ) {
        watchLaterService.removeFromWatchLater(userId, videoId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
