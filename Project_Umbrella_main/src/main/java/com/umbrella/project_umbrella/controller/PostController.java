package com.umbrella.project_umbrella.controller;

import com.umbrella.project_umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.project_umbrella.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    @PostMapping("/post/save")
    public Long save(@RequestBody PostSaveRequestDto requestDto){
        return postService.save(requestDto);
    }
}
