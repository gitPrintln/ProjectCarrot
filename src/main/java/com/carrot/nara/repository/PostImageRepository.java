package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Integer>  {

    // postId를 기준으로 첫 번째 결과값 가져오기 위함
    PostImage findFirstByPostId(Integer postId);

    List<PostImage> findByPostId(Integer postId);
    
}
