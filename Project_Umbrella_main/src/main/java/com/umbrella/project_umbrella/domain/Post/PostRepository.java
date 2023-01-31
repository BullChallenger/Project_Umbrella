package com.umbrella.project_umbrella.domain.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("select p from Post p ORDER BY p.id desc")
    List<Post> findAllDesc();

}
