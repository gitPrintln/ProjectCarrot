package com.carrot.nara.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.PostLike;
import com.carrot.nara.repository.PostLikeRepository;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageService {
    
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    
    /**
     * DB에 좋아요 상태를 변경하고 해당 글의 관심수를 변경해줌.
     * @param id userId
     * @param postId postId
     * @return 변경 완료 후 좋아요 상태
     */
    @Transactional
    public String likeStatusChg(Integer id, Integer postId) {
        log.info("likeStatusChg()");
        String likeStatus = "좋아요";
        
        // 좋아요 누른 것이 DB에 조회된다면? DB에 삭제 해주고, 상태를 좋아요 취소로 바꿔줌.
        Optional<PostLike> postLikeByUser = Optional.ofNullable(postLikeRepository.findByUserIdAndPostId(id, postId));
        if(postLikeByUser.isPresent()) {
            postLikeRepository.deleteById(postLikeByUser.get().getId());
            postRepository.uplikesCancel(postId);
            return likeStatus = "좋아요 취소";
        }
        
        // 좋아요되어 있지 않은 상태라면? DB에 추가 해주고 좋아요 상태로 전달해줌.
        PostLike entity = PostLike.builder().userId(id).postId(postId).build();
        postLikeRepository.save(entity);
        postRepository.uplikes(postId);
        return likeStatus;
    }

    /**
     * 좋아요 상태를 조회
     * @param id userId
     * @param postId postId
     * @return
     */
    @Transactional(readOnly = true)
    public String getPostLike(Integer id, Integer postId) {
        log.info("getPostLike()");
        String likeStatus = "좋아요 아님";
        PostLike postLike = postLikeRepository.findByUserIdAndPostId(id, postId);
        if(postLike != null) {
            likeStatus = "좋아요";
        }
        return likeStatus;
    }

}
