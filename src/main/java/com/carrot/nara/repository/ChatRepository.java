package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer>   {

    // 내 대화 목록들을 불러옴(최신순)
    List<Chat> findByUserIdOrSellerIdOrderByModifiedTimeDesc(Integer userId, Integer sellerId);

    // userId, postId, sellerId로 채팅방 불러오기
    Chat findByUserIdAndPostIdAndSellerId(Integer userId, Integer postId, Integer sellerId);


}
