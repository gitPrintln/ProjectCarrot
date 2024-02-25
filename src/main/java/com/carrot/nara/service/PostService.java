package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.domain.PostLike;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostLikeRepository;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    
    // 모든 포스트글들을 최신순으로 불러와서 리스트를 전달.
    @Transactional(readOnly = true)
    public List<Post> readAllByUpdateTime(){
        log.info("readAllByUpdateTime()");
        return postRepository.findAllByOrderByModifiedTimeDesc();
    }
    
    // 검색 키워드에 해당하는 포스트글들을 최신순으로 불러와서 리스트를 전달.
    @Transactional(readOnly = true)
    public List<Post> readByKeywordByUpdateTime(String keyword) {
        log.info("readByKeywordByUpdateTime()");
        return postRepository.findByTitleContainingOrContentContainingOrderByModifiedTimeDesc(keyword, keyword);
    }
    
    // 해당 post 글에 있는 첫 번째 이미지(썸네일)
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

    // 해당 포스트 글의 chat 카운트를 1올려줌.
    @Transactional
    public void postChatPlus(Integer postId) {
        log.info("postChatPlus()");
        postRepository.upChats(postId);
    }

    // 해당 포스트 글의 관심 수를 가져옴.
    @Transactional(readOnly = true)
    public Integer readWishCounts(Integer postId) {
        log.info("readWishCounts()");
        return postRepository.findById(postId).get().getWishCount();
    }

    // userId에 해당하는 post(내 판매)글 모두 불러오기
    @Transactional(readOnly = true)
    public List<Post> readAllMySellList(Integer userId) {
        log.info("readAllMySellList()");
        return postRepository.findByUserIdOrderByModifiedTimeDesc(userId);
    }

    // userId에 해당하는 post(내 판매)글 모두 불러오기
    @Transactional(readOnly = true)
    public List<Post> readAllMyLikeList(Integer userId) {
        log.info("readAllMyLikeList()");
        List<Post> list = new ArrayList<>();
        List<PostLike> likeList = postLikeRepository.findByUserId(userId);
        for (PostLike pl : likeList) {
            list.add(postRepository.findById(pl.getPostId()).get());
        }
        // 수정 시간순으로 list 정렬
        list.sort(Comparator.comparing(Post::getModifiedTime).reversed());
        return list;
    }
    
}
