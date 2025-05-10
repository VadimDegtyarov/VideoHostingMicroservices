package ru.website.micro.searchservice.model;
import jakarta.persistence.*;
import lombok.*;

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
    private Set<VideoHibernate> videos = new HashSet<>();

}