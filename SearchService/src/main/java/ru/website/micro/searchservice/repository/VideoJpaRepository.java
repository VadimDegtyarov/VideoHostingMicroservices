package ru.website.micro.searchservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.searchservice.model.VideoHibernate;

public interface VideoJpaRepository extends JpaRepository<VideoHibernate,Long> {
}
