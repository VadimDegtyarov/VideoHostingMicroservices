package ru.website.micro.searchservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.website.micro.searchservice.model.VideoElasticsearch;
import ru.website.micro.searchservice.repository.VideoElasticSearchRepository;
import ru.website.micro.searchservice.repository.VideoRepositoryCustom;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final VideoElasticSearchRepository repo;
    private final VideoRepositoryCustom elasticRepo;


    public Page<VideoElasticsearch> searchVideos(String text,
                                                 LocalDate releaseDateFrom,
                                                 LocalDate releaseDateTo,
                                                 Pageable pageable) {
        return elasticRepo.search(
                text,
                releaseDateFrom,
                releaseDateTo,
                pageable);
    }

    public List<String> suggestQueries(String prefix) {
        return elasticRepo.suggest(prefix);
    }
}
