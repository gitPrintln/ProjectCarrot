package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrot.nara.domain.Message;

public interface MessageRespository extends JpaRepository<Message, Integer> {

    // chatId에 해당하는 모든 메세지 가져오기
    List<Message> findAllByChatId(Integer chatId);

    // ChatId에 해당하는 메세지 전부 삭제
    void deleteByChatId(Integer id);

    // chatId에 해당하는 lastChat 가져옴
    Message findFirstByChatIdOrderByModifiedTimeDesc(Integer chatId);

    // 해당 chatId의 userNick을 제외한 메세지의 읽음으로 처리.(usernick != 이라고 표현해도 되지만, 표준으로는 <>을 같지않음으로 표시)
    @Query(value = "UPDATE MESSAGE m SET m.readChk = 0 WHERE m.chatId = :chatId and m.senderNickName <> :senderNick")
    @Modifying
    void unreadToReadMessage(@Param(value = "chatId") Integer chatId, @Param(value = "senderNick") String senderNick);

}
