package ru.website.micro.recommendationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.website.micro.recommendationservice.dto.VideoDTO;
import ru.website.micro.recommendationservice.enums.ReactionType;
import ru.website.micro.recommendationservice.enums.TargetType;
import ru.website.micro.recommendationservice.model.Video;
import ru.website.micro.recommendationservice.model.userInteractions.Tag;
import ru.website.micro.recommendationservice.repository.*;
import ru.website.micro.recommendationservice.repository.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final VideoRepository videoRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final NotInterestedRepository notInterestedRepository;
    private final TimeVideoWatchingRepository timeVideoWatchingRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final TagRepository tagRepository;

    private static final double REACTION_LIKE_WEIGHT = 0.7;
    private static final double REACTION_DISLIKE_WEIGHT = 0.7;
    private static final double TIME_WATCH_WEIGHT = 0.6;
    private static final double SUBSCRIBE_WEIGHT = 0.8;
    private static final double TAG_WEIGHT = 0.6;

    public Page<VideoDTO> videosRecommendations(Pageable pageable) {
        int need = pageable.getPageSize();
        List<Video> page = new ArrayList<>(videoRepository.findRandomUnseen(need));
        return getVideoDTOS(pageable, page);
    }

    private Page<VideoDTO> getVideoDTOS(Pageable pageable, List<Video> page) {
        List<VideoDTO> pageDTOs = page.stream().map(el -> VideoDTO.builder()
                .durationVideo(el.getDurationVideo())
                .videoName(el.getVideoName())
                .authorUsername(el.getAuthor().getUsername())
                .videoId(el.getId())
                .userId(el.getAuthor().getId())
                .numOfViews(el.getNumOfViews())
                .createdAt(el.getCreatedAt())
                .build()).toList();
        return new PageImpl<>(pageDTOs, pageable, pageDTOs.size());
    }

    @Cacheable(value = "recommendations", key = "#userId + '-' + #pageable.pageNumber", unless = "#result.isEmpty()")
    public Page<VideoDTO> videosRecommendations(UUID userId, Pageable pageable) {
        List<Video> page = getRecommendations(userId, pageable);
        if (page.size() < pageable.getPageSize()) {
            int need = pageable.getPageSize() - page.size();
            Set<Long> watched = new HashSet<>(watchHistoryRepository.findTargetIdsByUser(userId));
            Set<Long> notInt = new HashSet<>(notInterestedRepository.findTargetIdsByUser(userId));
            page.addAll(videoRepository.findRandomUnseen(watched, notInt, need));
        }
        return getVideoDTOS(pageable, page);
    }

    private List<Video> getRecommendations(UUID userId, Pageable pageable) {
        Set<Long> liked = new HashSet<>(reactionRepository.findTargetIdsByUserAndType(userId, TargetType.VIDEO, ReactionType.LIKE));
        Set<Long> disliked = new HashSet<>(reactionRepository.findTargetIdsByUserAndType(userId, TargetType.VIDEO, ReactionType.DISLIKE));
        Set<Long> notInterested = new HashSet<>(notInterestedRepository.findTargetIdsByUser(userId));
        Set<Long> watched = new HashSet<>(watchHistoryRepository.findTargetIdsByUser(userId));
        Map<Long, Integer> watchTime = timeVideoWatchingRepository.findDurationsByUser(userId)
                .stream().collect(Collectors.toMap(TimeVideoWatchingRepository.VideoDuration::getVideoId, TimeVideoWatchingRepository.VideoDuration::getDuration));

        Map<Long, Double> scores = new HashMap<>();

        // 2.1: лайки от похожих пользователей
        for (Long vid : liked) {
            UUID other = reactionRepository.findUserIdByTargetIdAndTargetType(vid, TargetType.VIDEO, ReactionType.LIKE);
            if (other == null) continue;
            for (Long cand : reactionRepository.findTargetIdsByUserAndType(other, TargetType.VIDEO, ReactionType.LIKE)) {
                if (watched.contains(cand) || notInterested.contains(cand)) continue;
                scores.merge(cand, REACTION_LIKE_WEIGHT, Double::sum);
            }
        }

        // 2.2: дизлайки от похожих
        for (Long vid : disliked) {
            UUID other = reactionRepository.findUserIdByTargetIdAndTargetType(vid, TargetType.VIDEO, ReactionType.DISLIKE);
            if (other == null) continue;
            for (Long cand : reactionRepository.findTargetIdsByUserAndType(other, TargetType.VIDEO, ReactionType.DISLIKE)) {
                if (watched.contains(cand) || notInterested.contains(cand)) continue;
                scores.merge(cand, -REACTION_DISLIKE_WEIGHT, Double::sum);
            }
        }

        // 2.3: время просмотра
        watchTime.forEach((vid, dur) -> {
            int full = videoRepository.getDuration(vid);
            if (dur >= full / 3 && !watched.contains(vid) && !notInterested.contains(vid)) {
                scores.merge(vid, TIME_WATCH_WEIGHT, Double::sum);
            }
        });

        // 2.4: подписки
        userRepository.findById(userId).ifPresent(u ->
                u.getSubscription().forEach(author ->
                        videoRepository.findAllByAuthorId(author.getId()).stream()
                                .map(Video::getId)
                                .filter(id -> !watched.contains(id) && !notInterested.contains(id))
                                .forEach(id -> scores.merge(id, SUBSCRIBE_WEIGHT, Double::sum))
                )
        );

        // 2.5: теги
        if (!liked.isEmpty()) {
            Set<Long> tagIds = tagRepository.findByVideoIds(liked).stream().map(Tag::getId).collect(Collectors.toSet());
            tagRepository.findVideoIdsByTagIds(tagIds).stream()
                    .filter(id -> !watched.contains(id) && !notInterested.contains(id))
                    .forEach(id -> scores.merge(id, TAG_WEIGHT, Double::sum));
        }

        // Сортировка по убыванию веса
        List<Long> sorted = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Video> vids = videoRepository.findAllById(sorted);
        Map<Long, Video> map = vids.stream().collect(Collectors.toMap(Video::getId, v -> v));
        List<Video> ordered = sorted.stream().map(map::get).collect(Collectors.toList());

        int start = Math.min((int) pageable.getOffset(), ordered.size());
        int end = Math.min(start + pageable.getPageSize(), ordered.size());
        return ordered.subList(start, end);
    }
}
