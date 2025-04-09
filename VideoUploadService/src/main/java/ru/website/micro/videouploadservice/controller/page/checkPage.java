package ru.website.micro.videouploadservice.controller.page;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.website.micro.videouploadservice.model.Video;
import ru.website.micro.videouploadservice.service.VideoService;

import java.util.UUID;

@Controller()
public class checkPage{
    @GetMapping("/user/{userId}/upload-video")
    public String uploadVideo(@PathVariable UUID userId, Model model)
    {
        model.addAttribute("userId",userId);
        return "index";
    }
    @GetMapping("/user/{userId}/get-video/{videoId}")
    public String getVideo(@PathVariable UUID userId,@PathVariable Long videoId, Model model)
    {
        model.addAttribute("userId",userId);
        model.addAttribute("videoId",videoId);
        return "watching";
    }

}
