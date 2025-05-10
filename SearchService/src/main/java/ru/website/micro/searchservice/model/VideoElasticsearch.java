package ru.website.micro.searchservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Document(indexName = "videos")
@Setting(settingPath = "/elasticsearch/settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoElasticsearch {

    @Id
    @JsonProperty("video_no")
    private String videoNo;

    @Field(type = FieldType.Text,
            name = "video_name",
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "standard")
    @JsonProperty("video_name")
    private String videoName;

    @Field(type = FieldType.Text, name = "tags")
    private List<String> tags;

    @Field(type = FieldType.Text,
            name = "transcript",
            analyzer = "russian")
    @JsonProperty("transcript")
    private String autoTranscript;

    @Field(type = FieldType.Keyword, name = "type")
    private String category;

    @Field(type = FieldType.Date, name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Field(type = FieldType.Text, name = "thumbnail_url")
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @Field(type = FieldType.Integer, name = "total_time_watching")
    @JsonProperty("total_time_watching")
    private Integer totalTimeWatching;

    @Field(type = FieldType.Keyword, name = "author_id")
    @JsonProperty("author_id")
    private UUID authorId;

    @CompletionField
    private Completion suggest;
}
