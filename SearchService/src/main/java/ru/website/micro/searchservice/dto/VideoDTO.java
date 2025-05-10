package ru.website.micro.searchservice.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.website.micro.searchservice.model.Tag;
import ru.website.micro.searchservice.model.user.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private Long id;
    private String videoName;
    private LocalDate createdAt;
    private String thumbnailUrl;
    private Integer totalTimeWatching = 0;
    private Integer numOfSubsOfAuthor;
    private String usernameOfAuthor;
    private String description;
}
