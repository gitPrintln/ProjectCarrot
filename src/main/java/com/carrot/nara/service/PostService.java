package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    
    
    // 모든 포스트글들을 최신순으로 불러와서 리스트를 전달.
    @Transactional(readOnly = true)
    public List<Post> readAllByUpdateTime(){
        log.info("readAllByUpdateTime()");
        return postRepository.findAllByOrderByModifiedTimeDesc();
    }
    
    // 모든 포스트글들을 최신순으로 불러와서 리스트를 전달.
    @Transactional(readOnly = true)
    public PostImage readThumbnail(Integer postId){
        log.info("readThumbnail()");
        return postImageRepository.findFirstByPostId(postId);
    }

    // Id로 그에 해당하는 post 정보 불러오기(상세보기)
    @Transactional(readOnly = true)
    public Post readByPostId(Integer id) {
        log.info("readByPostId(), id={}", id);
        return postRepository.findById(id).get();
    }
    
    // Id로 그에 해당하는 PostImages 모두 불러오기(상세보기)
    @Transactional(readOnly = true)
    public List<PostImage> readImgsByPostId(Integer id) {
        log.info("readImgsByPostId(), id={}", id);
        return postImageRepository.findByPostId(id);
    }
    
}
