package com.umbrella.project_umbrella.service;

import com.umbrella.project_umbrella.domain.Post.PostRepository;
import com.umbrella.project_umbrella.dto.post.PostSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Long save(PostSaveRequestDto requestDto){

        return postRepository.save(requestDto.toEntity()).getId();
    }

}
