package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    // userId와 postId에 일치하는 좋아요 누른 게 있는지 찾아옴.
    PostLike findByUserIdAndPostId(Integer userId, Integer postId);

    // 해당 userId가 좋아요를 누른 관심 목록 list
    List<PostLike> findByUserId(Integer userId);

}
