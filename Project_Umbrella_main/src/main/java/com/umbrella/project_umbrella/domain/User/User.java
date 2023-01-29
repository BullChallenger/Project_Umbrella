package com.umbrella.project_umbrella.domain.User;

import com.umbrella.project_umbrella.constant.Role;
import com.umbrella.project_umbrella.domain.Comment.Comment;
import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.dto.user.UserUpdateDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        @Column(nullable = false, unique = true)
        private String email;
        @Column(nullable = false)
        private String nickName;
        @Column(nullable = false)
        private String password;
        @Column(nullable = false)
        private String mName;
        @Column(nullable = false)
        private int age;
        @Column
        @Enumerated(EnumType.STRING)
        private Role role;

        @Column(length = 100)
        private String refreshToken;

        @Builder
        public User(String email, String nickName, String password, String mName, Integer age, Role role) {
            Assert.hasText(email, "email must not be blank");
            Assert.hasText(nickName, "nickName must not be blank");
            Assert.hasText(password, "password must not be blank");
            Assert.hasText(mName, "mName must not be blank");
            Assert.notNull(age, "age must not be null");
            Assert.notNull(role, "role must not be null");

            this.email = email;
            this.nickName = nickName;
            this.password = password;
            this.mName = mName;
            this.age = age;
            this.role = role;
        }

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Post> postList = new ArrayList<>();
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Comment> commentList = new ArrayList<>();

        public void addPost(Post post) {
            postList.add(post);
        }
        public void addComment(Comment comment) {
            commentList.add(comment);
        }

        public void updateMember (UserUpdateDto userUpdateDto) {
            this.email = userUpdateDto.getEmail();
            this.nickName = userUpdateDto.getNickName();
            this.mName = userUpdateDto.getMName();
            this.age = userUpdateDto.getAge();
        }

        public void updateRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void destroyRefreshToken() {
            this.refreshToken = null;
        }

        public void encodePassword(PasswordEncoder passwordEncoder) {
            this.password = passwordEncoder.encode(password);
        }

}
