package com.carrot.nara.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Post;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HitsService {
    
    private final PostRepository postRepository;
    
    /**
     * 해당 글의 조회수를 1올려줌.
     * @param id 조회수를 올릴 postId
     */
    @Transactional
    public void postHitsUp(Integer id) {
        log.info("postHitsUp(id={})",id);
        Post post = postRepository.findById(id).get();
        post.upHits();
    }

    
}
