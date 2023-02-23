package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Comment.Comment;
import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponseDto {

    private Long id;

    private String writer;

    private String title;

    private List<Comment> comments;
    private User user;



    public PostListResponseDto(Post post){
        this.id = post.getId();
        this.writer = post.getWriter();
        this.title = post.getTitle();
        this.user = post.getUser();
    }

}
