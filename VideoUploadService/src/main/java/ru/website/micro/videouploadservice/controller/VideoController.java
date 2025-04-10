package ru.website.micro.videouploadservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.videouploadservice.dto.ImportVideoDto;
import ru.website.micro.videouploadservice.service.VideoService;
import java.io.IOException;
import java.util.UUID;

@RestController()
@RequestMapping("/api/v1/users/{userId}/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

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



}
