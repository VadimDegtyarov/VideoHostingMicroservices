package ru.website.micro.videouploadservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.website.micro.videouploadservice.model.Comment;


public interface CommentRepository extends JpaRepository<Comment,Long> {
}
