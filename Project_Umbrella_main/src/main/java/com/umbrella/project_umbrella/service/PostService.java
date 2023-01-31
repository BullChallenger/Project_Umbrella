package com.umbrella.project_umbrella.service;


import com.umbrella.project_umbrella.domain.Post.Post;
import com.umbrella.project_umbrella.domain.Post.PostRepository;
import com.umbrella.project_umbrella.dto.post.PostListResponseDto;
import com.umbrella.project_umbrella.dto.post.PostResponseDto;
import com.umbrella.project_umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.project_umbrella.dto.post.PostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional

public class PostService {
    private final PostRepository postRepository;


    // 저장 메서드
    public Long save(PostSaveRequestDto requestDto){

        return postRepository.save(requestDto.toEntity()).getId();
    }


    // 수정 메서드
    public Long update(Long id, PostUpdateRequestDto requestDto){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        post.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }

    // 삭제 메서드
    public Long delete(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        postRepository.delete(post);

        return id;
    }

    // 게시글 클릭 메서드
    public PostResponseDto findById(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return new PostResponseDto(post);
    }

    // 게시글 전체 리턴 메서드
    @Transactional(readOnly = true)
    public List<PostListResponseDto> findAllDesc(){
        return postRepository.findAllDesc().stream()
                .map(PostListResponseDto::new)
                .collect(Collectors.toList());
    }
}
