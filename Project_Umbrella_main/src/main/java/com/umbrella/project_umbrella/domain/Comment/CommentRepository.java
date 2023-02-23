package com.umbrella.project_umbrella.domain.Comment;

import com.umbrella.project_umbrella.domain.Post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("select p from Comment p ORDER BY p.id desc")
    List<Comment> findAllDesc();
}
