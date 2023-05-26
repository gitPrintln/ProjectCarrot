package com.carrot.nara.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Integer>  {

    // postId를 기준으로 첫 번째 결과값
    PostImage findFirstByPostId(Integer postId);


}