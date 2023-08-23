package com.carrot.nara.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Integer>  {

    // List, Home에서 사용될 postimage불러올 메서드(썸네일용으로 첫 이미지만 가져옴.)
    PostImage findFirstByPostId(Integer postId);

    List<PostImage> findByPostId(Integer postId);

    // 해당 파일네임의 데이터를 삭제
    void deleteByFileName(String fileName);

    // 해당 포스트 ID의 데이터를 삭제
    void deleteByPostId(Integer id);
    
}
