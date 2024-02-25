package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrot.nara.domain.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 최신글의 postId들을 불러오기(최신순) 판매중인 것만 가져옴.(예약중, 판매완료 X)
    @Query(value = "SELECT p.id FROM POST p WHERE STATUS = '판매중' ORDER BY p.modified_time desc", nativeQuery = true)
    List<Integer> findRecentPostIds();

    // 최신글들 모두 불러오기(최신순)
    List<Post> findAllByOrderByModifiedTimeDesc();
    
    // 해당 id의 조회수 1증가
    @Query(value = "UPDATE POST p SET p.hits = p.hits +1 WHERE p.id = :postId")
    @Modifying
    void upHits(@Param(value = "postId") Integer postId);
    
    // 해당 id의 채팅수 1증가
    @Query(value = "UPDATE POST p SET p.chats = p.chats +1 WHERE p.id = :postId")
    @Modifying
    void upChats(@Param(value = "postId") Integer postId);

    // 해당 id의 관심수 1증가
    @Query(value = "UPDATE POST p SET p.wishCount = p.wishCount +1 WHERE p.id = :postId")
    @Modifying
    void uplikes(@Param(value = "postId") Integer postId);
    
    // 해당 id의 관심수 1취소
    @Query(value = "UPDATE POST p SET p.wishCount = p.wishCount -1 WHERE p.id = :postId")
    @Modifying
    void uplikesCancel(@Param(value = "postId") Integer postId);
    
    // title, content에서 keyword를 포함하는 % + keyword + %를 업데이트 순으로 정렬한 리스트
    List<Post> findByTitleContainingOrContentContainingOrderByModifiedTimeDesc(String keyword, String keyword2);
    
    // 해당 id의 status를 수정
    @Query(value = "UPDATE POST p SET p.status = :status WHERE p.id = :postId")
    @Modifying
    void updateStatus(@Param(value = "postId") Integer postId, @Param(value = "status") String status);

    // userId에 해당하는 post글 모두 불러오기
    List<Post> findByUserIdOrderByModifiedTimeDesc(Integer userId);
}
