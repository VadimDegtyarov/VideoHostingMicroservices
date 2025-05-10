package ru.website.micro.searchservice.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.website.micro.searchservice.model.VideoElasticsearch;

public interface VideoElasticSearchRepository extends ElasticsearchRepository<VideoElasticsearch, String> {
}
