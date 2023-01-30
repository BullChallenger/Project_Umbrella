package com.umbrella.project_umbrella.dto.post;

import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;

public class PostSaveRequestDto {

    private String writer;

    private String title;

    private String content;


    @Builder
    public PostSaveRequestDto(String writer, String title, String content){
        this.writer = writer;
        this.title = title;
        this.content = content;
    }

    public Post toEntity(){
        return Post.builder().
                writer(writer).
                title(title).
                content(content).
                build();
    }

}
