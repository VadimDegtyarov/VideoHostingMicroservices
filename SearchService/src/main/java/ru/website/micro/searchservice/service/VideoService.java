package ru.website.micro.searchservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import ru.website.micro.searchservice.dto.VideoDTO;
import ru.website.micro.searchservice.exception.ResourceNotFoundException;
import ru.website.micro.searchservice.model.VideoElasticsearch;
import ru.website.micro.searchservice.model.VideoHibernate;
import ru.website.micro.searchservice.repository.VideoJpaRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;


@RequiredArgsConstructor
@Service
public class VideoService {
    private final VideoJpaRepository videoJpaRepository;

    public Page<VideoDTO> getVideos(Page<VideoElasticsearch> videoPage) {
        List<VideoDTO> videos = videoPage.getContent().stream().map(el -> {
            VideoHibernate videoHibernate = videoJpaRepository.findById(Long.valueOf(el.getVideoNo())).orElseThrow(() -> new ResourceNotFoundException("Ошибка поиска видео"));
            return VideoDTO.builder()
                    .id(videoHibernate.getId())
                    .videoName(videoHibernate.getVideoName())
                    .createdAt(videoHibernate.getCreatedAt())
                    .description(videoHibernate.getDescription())
                    .numOfSubsOfAuthor(videoHibernate.getAuthor().getNumOfSubs())
                    .thumbnailUrl(videoHibernate.getThumbnailUrl())
                    .totalTimeWatching(videoHibernate.getTotalTimeWatching())
                    .usernameOfAuthor(videoHibernate.getAuthor().getUsername())
                    .build();
        }).toList();
        return new PageImpl<>(videos, videoPage.getPageable(), videoPage.getTotalElements());
    }
}
