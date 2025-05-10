package ru.website.micro.searchservice.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SuggestMode;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.PhraseSuggestOption;
import co.elastic.clients.elasticsearch.core.search.TermSuggestOption;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.website.micro.searchservice.model.VideoElasticsearch;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryCustomImpl implements VideoRepositoryCustom {

    private final ElasticsearchClient client;

    @Override
    public Page<VideoElasticsearch> search(
            String text,
            LocalDate releaseDateFrom,
            LocalDate releaseDateTo,
            Pageable pageable
    ) {
        try {
            Query multiMatch = MultiMatchQuery.of(m -> m
                    .query(text)
                    .fields("video_name", "tags", "transcript")
                    .fuzziness("AUTO")               // авто‑уровень нестрогого соответствия
                    .prefixLength(1)                         // первые N символов точные
                    .minimumShouldMatch("70%")               // минимум 70% совпадающих терминов
            )._toQuery();

            BoolQuery.Builder bool = new BoolQuery.Builder().must(multiMatch);
            if (releaseDateFrom != null || releaseDateTo != null) {
                bool.filter(f -> f.range(r -> {
                    r.field("created_at");
                    if (releaseDateFrom != null) r.gte(JsonData.of(releaseDateFrom.toString()));
                    if (releaseDateTo != null) r.lte(JsonData.of(releaseDateTo.toString()));
                    return r;
                }));
            }


            List<SortOptions> sortOptions = pageable.getSort().stream()
                    .map(order -> SortOptions.of(so -> so
                            .field(f -> f
                                    .field(order.getProperty())
                                    .order(order.isAscending() ? SortOrder.Asc : SortOrder.Desc)
                            )
                    ))
                    .collect(Collectors.toList());

            SearchResponse<VideoElasticsearch> resp = client.search(s -> s
                            .index("videos")
                            .from(pageable.getPageNumber() * pageable.getPageSize())
                            .size(pageable.getPageSize())
                            .query(q -> q.bool(bool.build()))
                            .sort(sortOptions),
                    VideoElasticsearch.class
            );

            List<VideoElasticsearch> list = resp.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
            long total = resp.hits().total().value();
            return new PageImpl<>(list, pageable, total);

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch search failed", e);
        }
    }

    @Override
    public List<String> suggest(String prefix) {
        try {
            SearchResponse<Void> resp = client.search(s -> s
                            .index("videos")
                            .suggest(su -> su
                                    .suggesters("term-suggest", sg -> sg
                                            .text(prefix)
                                            .term(t -> t
                                                    .field("video_name")
                                                    .suggestMode(SuggestMode.Always)
                                                    .size(5)
                                            )
                                    )
                                    .suggesters("phrase-suggest", ph -> ph
                                            .text(prefix)
                                            .phrase(p -> p
                                                    .field("video_name")
                                                    .gramSize(3)
                                                    .size(5)
                                            )
                                    )
                                    .suggesters("video-suggest", cm -> cm
                                            .text(prefix)
                                            .completion(c -> c
                                                    .field("suggest")
                                                    .size(8)
                                                    .fuzzy(fz -> fz.fuzziness("AUTO").minLength(1).prefixLength(1))
                                            )
                                    )
                            ),
                    Void.class);

            return resp.suggest().values().stream()
                    .flatMap(List::stream)
                    .flatMap(sug -> {
                        switch (sug._kind()) {
                            case Term:
                                return sug.term().options().stream().map(TermSuggestOption::text);
                            case Phrase:
                                return sug.phrase().options().stream().map(PhraseSuggestOption::text);
                            case Completion:
                                return sug.completion().options().stream().map(CompletionSuggestOption::text);
                            default:
                                return Stream.empty();
                        }
                    })
                    .distinct()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch suggest failed", e);
        }
    }

}
