package ru.website.micro.videouploadservice.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "video_quality")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String quality; // 1080p, 720p, 480p

    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Video video;
}