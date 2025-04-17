package ru.website.micro.videouploadservice.controller.page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.service.VideoService;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller()
@RequiredArgsConstructor
@Slf4j
public class checkPage {
    private final VideoService videoService;

    @GetMapping("/user/{userId}/upload-video")
    public String uploadVideo(@PathVariable UUID userId, Model model) {
        model.addAttribute("userId", userId);
        return "index";
    }

    @GetMapping("/get-video/{videoId}")
    public String getVideo(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "1080p") String quality,
            Model model) {

        try {
            model.addAttribute("videoMimeType", videoService.getMimeType(videoId, quality));
            model.addAttribute("quality", quality);

            model.addAttribute("userId", videoService.getVideoById(videoId).getAuthor().getId());
            model.addAttribute("videoId", videoId);
            model.addAttribute("availableQualities",
                    videoService.getAvailableQualities(videoId));
            return "watching";
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка определения типа видео",
                    e
            );
        }
    }
    @GetMapping("/user/videos")
    public String showVideosPage(
            @RequestParam(required = false) Boolean isChecked,
            @RequestParam(required = false) Long lastVideoId,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
            Model model) {

        Page<Video> videos = videoService.getVideos(isChecked, lastVideoId, pageable);

        model.addAttribute("videos", videos);
        model.addAttribute("isChecked", isChecked);
        model.addAttribute("lastVideoId", lastVideoId);

        return "videos";
    }
}
