package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.Message;

public interface MessageRespository extends JpaRepository<Message, Integer> {

    // chatId에 해당하는 모든 메세지 가져오기
    List<Message> findAllByChatId(Integer chatId);

}
