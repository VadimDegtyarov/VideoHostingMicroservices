package ru.website.micro.videoprocessingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.website.micro.videoprocessingservice.service.StreamingService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VideoStreamController {
    private final StreamingService streamingService;

    @GetMapping("/video/{videoId}/stream")
    public Mono<ResponseEntity<byte[]>> streamVideo(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "1080p") String quality,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        return streamingService.getVideoChunk(videoId, quality, rangeHeader)
                .map(chunk -> ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .header(HttpHeaders.CONTENT_TYPE, chunk.getContentType())
                        .header(HttpHeaders.CONTENT_RANGE, chunk.getRange())
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(chunk.getData().length))
                        .body(chunk.getData()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
