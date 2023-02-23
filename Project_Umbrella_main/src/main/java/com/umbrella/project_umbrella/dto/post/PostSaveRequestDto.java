package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;

public class PostSaveRequestDto {


    private String title;

    private String content;

    private User user;


    @Builder
    public PostSaveRequestDto( String writer, String title, String content, User user){
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public Post toEntity(){
        return Post.builder().
                title(title).
                content(content).
                user(user).
                build();
    }

}
