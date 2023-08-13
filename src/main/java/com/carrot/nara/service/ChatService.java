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
        return chatRepository.findByUserIdOrderByModifiedTimeDesc(userId);
    }
    
    /**
     * chatId를 통해서 postId 가져옴
     * @param postId에 해당하는 chatId
     * @return chatId에 해당하는 postId
     */
    @Transactional(readOnly = true)
    public Integer getPostId(Integer chatId) {
        log.info("getPostId()");
        return chatRepository.findById(chatId).get().getPostId();
    }

    
    // userId, postId, sellerId로 연결한 채팅방 정보를 불러오기 위해
    @Transactional(readOnly = true)
    public Chat loadChat(Integer userId, Integer postId, Integer sellerId) {
        log.info("loadChat()");
        return chatRepository.findByUserIdAndPostIdAndSellerId(userId, postId, sellerId);
    }

    // 채팅방을 새로 만드는 메서드
    @Transactional
    public Chat createNewChat(Integer userId, Integer postId, Integer sellerId) {
        log.info("createNewChat()");
        Chat entity = Chat.builder().userId(userId).postId(postId).sellerId(sellerId).build();
        Chat newChat = chatRepository.save(entity);
        return newChat;
    }
    
    // chatId에 해당하는 채팅방 정보를 불러오기 위해
    @Transactional(readOnly = true)
    public Chat loadChat(Integer chatId) {
        log.info("loadChat()");
        return chatRepository.findById(chatId).get();
    }
    
}
