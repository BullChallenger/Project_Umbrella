package com.umbrella.project_umbrella.domain.Post;

import com.umbrella.project_umbrella.service.PostService;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {


    @Autowired
    PostRepository postRepository;

    @Test
    public void Save(){
        String content = "content";
        String title = "title";
        String writer = "writer";

        postRepository.save(Post.builder().
                content(content).
                title(title).
                writer(writer).build());

        List<Post> postList = postRepository.findAll();

        Post post = postList.get(0);

        Assertions.assertEquals(post.getTitle(),title);

    }

}