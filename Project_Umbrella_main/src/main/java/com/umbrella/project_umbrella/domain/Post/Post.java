package com.umbrella.project_umbrella.domain.Post;

import com.umbrella.project_umbrella.domain.Comment.Comment;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    @Column(nullable = true)
    private Long id;

    @Column(length = 500 , nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post" , fetch = FetchType.EAGER , cascade = CascadeType.REMOVE)
    private List<Comment> comment;


    @Builder
    public Post(String writer, String title,  String content){
        this.writer = writer;
        this.content = content;
        this.title = title;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}