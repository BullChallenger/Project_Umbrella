package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Comment.Comment;
import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDto {

    private Long id;
    private String title;

    private String writer;

    private String content;

    private User user;

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.writer = post.getWriter();
        this.content = post.getContent();
        this.user = post.getUser();
    }


}
