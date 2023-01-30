package com.umbrella.project_umbrella.domain.Comment;

import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT" , nullable = false)
    private String comment;

    @Column
    @CreatedDate
    private String createDate;

    @Column
    @LastModifiedDate
    private String modifiedDate;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
