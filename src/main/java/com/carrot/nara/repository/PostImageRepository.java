package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Integer>  {

    // postId를 기준으로 첫 번째 결과값
    PostImage findFirstByPostId(Integer postId);

    List<PostImage> findByPostId(Integer postId);
    
    // Home에 띄울 slide Image 5개
    List<PostImage> findTop5ByOrderByIdDesc();
}
