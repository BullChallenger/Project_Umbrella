package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private Long id;
    private String title;

    private String writer;

    private String content;

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter();
        this.content = post.getContent();
    }


}
