package ru.website.micro.searchservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.website.micro.searchservice.dto.VideoDTO;
import ru.website.micro.searchservice.model.VideoElasticsearch;
import ru.website.micro.searchservice.model.VideoHibernate;
import ru.website.micro.searchservice.service.SearchService;
import ru.website.micro.searchservice.service.VideoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final VideoService videoService;
    private final SearchService svc;


    @GetMapping
    public Page<VideoDTO> search(
            @RequestParam String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Pageable pageable
    ) {
        Page<VideoElasticsearch> searchingVideosId = svc.searchVideos(q, dateFrom, dateTo, pageable);
        return videoService.getVideos(searchingVideosId);
    }


    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String prefix) {
        return svc.suggestQueries(prefix);
    }
}
