package ru.website.micro.userengagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.userengagementservice.dto.NotInterestedDto;
import ru.website.micro.userengagementservice.service.NotInterestedService;

import java.util.UUID;

@RestController()
@RequestMapping("/api/v1/user/{userId}")
@RequiredArgsConstructor
public class NotInterestedController {
    private final NotInterestedService notInterestedService;

    @PostMapping("/video/{videoId}")
    public ResponseEntity<Void> toggleNotInterested(
            @PathVariable UUID userId,
            @PathVariable Long videoId
    ) {
        boolean added = notInterestedService.toggleNotInterested(userId, videoId);
        return ResponseEntity.status(added ? HttpStatus.CREATED : HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<Boolean> checkNotInterested(
            @PathVariable UUID userId,
            @PathVariable Long videoId
    ) {
        return ResponseEntity.ok(notInterestedService.isVideoInNotInterested(userId, videoId));
    }

}
