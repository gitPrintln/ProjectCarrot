package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrot.nara.domain.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer>   {

    // 내 대화 목록들을 불러옴(최신순)
    List<Chat> findByUserIdOrSellerIdOrderByModifiedTimeDesc(Integer userId, Integer sellerId);

    // userId, postId, sellerId로 채팅방 불러오기
    Chat findByUserIdAndPostIdAndSellerId(Integer userId, Integer postId, Integer sellerId);

    // postId로 삭제할 채팅방을 모두 찾기
    List<Chat> findByPostId(Integer id);

    // postID로 찾은 채팅방 모두 삭제
    void deleteByPostId(Integer id);

    /*
    // Test Query
    @Query(
            "SELECT c FROM CHAT c "
            + "JOIN (SELECT chatId, COALESCE(MAX(modifiedTime), 0) AS lastTime"
            + "      FROM MESSAGE "
            + "      WHERE chatId IN :chatIds "
            + "      GROUP BY chatId) lastChat "
            + "ON c.id = lastChat.chatId "
            + "ORDER BY lastChat.lastTime DESC"
    )
    List<Chat> testingQr(@Param(value = "chatIds") List<Integer> chatIds);*/
}
