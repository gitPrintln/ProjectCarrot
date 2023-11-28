package com.carrot.nara.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    // userId와 postId에 일치하는 좋아요 누른 게 있는지 찾아옴.
    PostLike findByUserIdAndPostId(Integer userId, Integer postId);

}
