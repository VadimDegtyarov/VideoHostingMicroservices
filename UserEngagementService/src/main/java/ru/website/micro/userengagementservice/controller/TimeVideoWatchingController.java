package ru.website.micro.userengagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userengagementservice.model.TimeVideoWatching;
import ru.website.micro.userengagementservice.service.TimeVideoWatchingService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/{userId}")
@RequiredArgsConstructor
public class TimeVideoWatchingController {
    private final TimeVideoWatchingService timeVideoWatchingService;

    @PostMapping("/video/{videoId}/watch-time")
    public ResponseEntity<TimeVideoWatching> trackWatchingTime(@PathVariable UUID userId, @PathVariable Long videoId, @RequestBody Integer duration) {
        return ResponseEntity.ok(timeVideoWatchingService.addingTimeWatching(userId, videoId, duration));
    }

    @PutMapping("/video/{videoId}/watch-time")
    public ResponseEntity<TimeVideoWatching> updateWatchingTime(@PathVariable UUID userId, @PathVariable Long videoId, @RequestBody Integer duration) {
        return ResponseEntity.ok(timeVideoWatchingService.updateTimeWatching(userId, videoId, duration));
    }

    @GetMapping("/video/{videoId}/")
    public ResponseEntity<Integer> getTimeWatching(@PathVariable UUID userId, @PathVariable Long videoId) {
        return ResponseEntity.ok(timeVideoWatchingService.getTimeWatching(userId, videoId));
    }
}
