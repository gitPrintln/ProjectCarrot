package com.carrot.nara.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.carrot.nara.domain.Message;
import com.carrot.nara.dto.MessageReadDto;
import com.carrot.nara.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MessageRepositoryTest {
    
    @Autowired
    private MessageRespository messageRespository;
    @Autowired
    private ChatService chatService;
    
//    @Test
    public void test() {
        Message entity = Message.builder().senderNickName("test").message("메세지 리포지토리 테스트중.")
                .build();
        messageRespository.save(entity);
        
        List<Message> m = messageRespository.findAll();
        for (Message message : m) {
            log.info(message.toString());
        }
    }
    
    @Test
    public void test2() {
        MessageReadDto m = MessageReadDto.builder().sender("보내는이").message("내용 테스트!!").build();
        chatService.newMessage(1, m);
    }
}
