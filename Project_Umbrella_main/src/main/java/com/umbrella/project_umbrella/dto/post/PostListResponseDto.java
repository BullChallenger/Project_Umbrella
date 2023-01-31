package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostListResponseDto {

    private Long id;

    private String writer;

    private String title;



    public PostListResponseDto(Post post){
        this.id = post.getId();
        this.writer = post.getWriter();
        this.title = post.getTitle();
    }

}
