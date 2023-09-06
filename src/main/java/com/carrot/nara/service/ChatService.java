package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Chat;
import com.carrot.nara.domain.Message;
import com.carrot.nara.dto.MessageCreateDto;
import com.carrot.nara.dto.MessageReadDto;
import com.carrot.nara.repository.ChatRepository;
import com.carrot.nara.repository.MessageRespository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRespository messageRespository;
    private final RedisService redisService;
    
    @Transactional(readOnly = true)
    public List<Chat> myChatList(Integer userId){
        log.info("myChatList()");
        // userId, sellerId 둘 중에 하나라도 속하는 채팅방을 모두 가져오기 위함.
        return chatRepository.findByUserIdOrSellerIdOrderByModifiedTimeDesc(userId, userId);
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
    
    // 메세지를 SQL에 저장(채팅방 id, 보낸이, 메세지 내용 순으로), lastChat의 redis 캐시를 수정해줌.
    @Transactional
    public void newMessage(Integer chatId, MessageReadDto dto) {
        log.info("newMessage()");
        // 메세지를 oracle sql DB에 저장.
        MessageCreateDto entity = MessageCreateDto.builder().chatId(chatId).senderNickName(dto.getSender())
                .message(dto.getMessage()).sendTime(dto.getSendTime()).build();
        messageRespository.save(entity.toEntity(chatId));
        // redis 캐시에 저장.
        redisService.modifiedLastChat(chatId, entity.getMessage());
    }

    // chatId에 해당하는 채팅 리스트를 불러옴.
    @Transactional(readOnly = true)
    public List<MessageReadDto> readHistory(Integer chatId) {
        log.info("readHistory()");
        List<Message> message = messageRespository.findAllByChatId(chatId);
        List<MessageReadDto> list = new ArrayList<>();
        for (Message m : message) {
            MessageReadDto entity = MessageReadDto.builder().sender(m.getSenderNickName()).message(m.getMessage())
                    .sendTime(m.getModifiedTime().toString()).build();
            list.add(entity);
        }
        return list;
    }

    // 안읽은 메세지를 읽음으로 처리 하기 위해서(message table에서 read 1->0)
    public void unreadToRead(Integer chatId, String userNick) {
        log.info("unreadToRead()");
        messageRespository.unreadToReadMessage(chatId, userNick);
    }
    
}
