package com.umbrella.project_umbrella.controller;

import com.umbrella.project_umbrella.dto.post.PostListResponseDto;
import com.umbrella.project_umbrella.dto.post.PostResponseDto;
import com.umbrella.project_umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.project_umbrella.dto.post.PostUpdateRequestDto;
import com.umbrella.project_umbrella.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Getter
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post/save")
    public Long save(@RequestBody PostSaveRequestDto requestDto){ // 게시물 & 댓글 가져오기
        return postService.save(requestDto);
    }

    @PutMapping("/post/update/{id}")
    public Long update(@PathVariable Long id, PostUpdateRequestDto requestDto){

        return postService.update(id, requestDto);
    }

    @GetMapping("/post/{id}")
    public PostResponseDto findById(@PathVariable Long id){
        return postService.findById(id);
    }

    @DeleteMapping("/post/delete/{id}")
    public Long delete(@PathVariable Long id){

        return postService.delete(id);
    }

    // 포스트 및 댓글 같이 가져오기


}
