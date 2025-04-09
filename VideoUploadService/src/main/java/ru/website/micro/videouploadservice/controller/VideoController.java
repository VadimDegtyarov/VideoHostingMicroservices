package ru.website.micro.videouploadservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.videouploadservice.dto.ImportVideoDto;
import ru.website.micro.videouploadservice.service.NotInterestedService;
import ru.website.micro.videouploadservice.service.TimeVideoWatchingService;
import ru.website.micro.videouploadservice.service.VideoService;
import ru.website.micro.videouploadservice.service.WatchLaterService;

import java.io.IOException;
import java.util.UUID;

@RestController()
@RequestMapping("/api/v1/users/{userId}/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final NotInterestedService notInterestedService;
    private final WatchLaterService watchLaterService;
    private final TimeVideoWatchingService timeVideoWatchingService;

    @PostMapping()
    public void uploadVideo(@PathVariable UUID userId, @ModelAttribute ImportVideoDto VideoDto) {
        videoService.uploadVideo(userId, VideoDto);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<InputStreamResource> getVideo(@PathVariable UUID userId, @PathVariable Long videoId) throws IOException {
        return videoService.getVideo(videoId);
    }

    @GetMapping("/{videoId}/thumbnail")
    public ResponseEntity<InputStreamResource> getThumbnail(@PathVariable UUID userId, @PathVariable Long videoId) {
        return videoService.getThumbnail(videoId);
    }

    @PostMapping("/{videoId}/not-interested")
    public HttpStatus markAsNotInterested(@PathVariable UUID userId, @PathVariable Long videoId) {
        return notInterestedService.addingNotInterested(userId, videoId);
    }

    @PostMapping("/{videoId}/watch-later")
    public HttpStatus addToWatchLater(@PathVariable UUID userId, @PathVariable Long videoId) {
        return watchLaterService.addingWatchLater(userId, videoId);
    }

    @PostMapping("/{videoId}/watch-time")
    public HttpStatus trackWatchingTime(@PathVariable UUID userId, @PathVariable Long videoId, @RequestBody Integer duration) {
        return timeVideoWatchingService.addingTimeWatching(userId, videoId,duration);
    }

}
