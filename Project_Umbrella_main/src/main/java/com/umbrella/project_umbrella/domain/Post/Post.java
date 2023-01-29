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

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "posts" , fetch = FetchType.EAGER , cascade = CascadeType.REMOVE)
    private List<Comment> comments;


    @Builder
    public Post(String title, String writer, String content){
        this.title = title;
        this.writer = writer;
        this.content = content;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}