package ru.website.micro.userengagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userengagementservice.model.Video;
import ru.website.micro.userengagementservice.service.WatchHistoryService;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/v1/users/{userId}/watch-history")
@RequiredArgsConstructor
public class WatchHistoryController {
    private final WatchHistoryService watchHistoryService;

    @PostMapping("/videos/{videoId}")
    public ResponseEntity<Void> toggleWatchHistory(
            @PathVariable UUID userId,
            @PathVariable Long videoId,
            @RequestParam(required = false) Integer progress) {
        boolean added = watchHistoryService.toggleWatchHistory(
                userId, videoId, progress != null ? progress : 0);
        return ResponseEntity.status(added ? HttpStatus.CREATED : HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Page<Video>> getWatchHistory(
            @PathVariable UUID userId,
            @PageableDefault(sort = "watchedAt", direction = DESC) Pageable pageable,
            @RequestParam Long lastVideoId) {
        return ResponseEntity.ok(watchHistoryService.getWatchHistory(userId, pageable, lastVideoId));
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<Boolean> isVideoInWatchHistory(
            @PathVariable UUID userId,
            @PathVariable Long videoId) {
        return ResponseEntity.ok(watchHistoryService.isVideoInWatchHistory(userId, videoId));
    }
}