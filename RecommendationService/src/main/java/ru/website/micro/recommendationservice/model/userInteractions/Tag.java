package ru.website.micro.recommendationservice.model.userInteractions;
import jakarta.persistence.*;
import lombok.*;
import ru.website.micro.recommendationservice.model.Video;


import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tags")
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<Video> videos = new HashSet<>();

}