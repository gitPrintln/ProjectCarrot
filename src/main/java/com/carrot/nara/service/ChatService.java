package com.carrot.nara.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Chat;
import com.carrot.nara.repository.ChatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    
    @Transactional(readOnly = true)
    public List<Chat> myChatList(Integer userId){
        log.info("myChatList()");
        return chatRepository.findByUserId(userId);
    }
    
    /**
     * chatId를 통해서 postId 가져옴
     * @param postId에 해당하는 chatId
     * @return chatId에 해당하는 postId
     */
    public Integer getPostId(Integer chatId) {
        log.info("getPostId()");
        return chatRepository.findById(chatId).get().getPostId();
    }
}
