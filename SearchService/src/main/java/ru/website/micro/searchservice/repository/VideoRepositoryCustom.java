package ru.website.micro.searchservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.website.micro.searchservice.model.VideoElasticsearch;

import java.time.LocalDate;
import java.util.List;

public interface VideoRepositoryCustom {
    Page<VideoElasticsearch> search(
            String text,
            LocalDate releaseDateFrom,
            LocalDate releaseDateTo,
            Pageable pageable
    );

    List<String> suggest(String prefix);
}
