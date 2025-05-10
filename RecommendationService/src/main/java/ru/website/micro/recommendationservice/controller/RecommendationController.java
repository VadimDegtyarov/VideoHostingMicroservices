package ru.website.micro.recommendationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.recommendationservice.dto.VideoDTO;
import ru.website.micro.recommendationservice.service.RecommendationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/personal")
    public ResponseEntity<Page<VideoDTO>> getPersonalRecommendations(
            @RequestHeader("X-User-Id")String userIdString ,
            Pageable pageable) {
        log.info(userIdString);
        if(userIdString==null){

            return ResponseEntity.ok(recommendationService.videosRecommendations(pageable));
        }
        else{
            UUID userId= UUID.fromString(userIdString);
            return ResponseEntity.ok(recommendationService.videosRecommendations(userId, pageable));
        }
    }
}