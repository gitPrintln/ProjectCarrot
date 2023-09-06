package com.carrot.nara.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.repository.MessageRespository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Autowired
    private MessageRespository messageRespository; // 캐시가 없으면 마지막 챗을 가져옴

    // redis에 캐시를 설정해둠.
    private void setCacheLastChat(Integer chatId, String lastChat) {
        log.info("cacheLastChat()");
        String id = "chatId: " + chatId;
        redisTemplate.opsForValue().set(id, lastChat);
    }

    // redis cache의 lastchat을 가져옴.
    private String getLastChatFromCache(Integer chatId) {
        log.info("getLastChatFromCache()");
        String id = "chatId: " + chatId;
        return redisTemplate.opsForValue().get(id);
    }
    
    /**
     * 1. redis 캐시 데이터를 확인 후 없으면 2. jpaRepository에서 찾아옴 + 캐시에 저장.
     * @param chatId 마지막 챗을 가져올 chatId
     * @return 마지막 챗
     */
    @Transactional(readOnly = true)
    public String getLastChat(Integer chatId) {
        log.info("getLastChat(id={})", chatId);
        String lastChat = getLastChatFromCache(chatId);
        if(lastChat == null) { // 캐시 데이터가 없으면 oracle sql에서 조회후 찾아와서 캐시에 저장.
            lastChat = messageRespository.findFirstByChatIdOrderByModifiedTimeDesc(chatId).getMessage();
            setCacheLastChat(chatId, lastChat);
        }
        return lastChat;
    }

    // lastChat이 바뀌었을 경우. redis 캐시도 수정해줌.
    public void modifiedLastChat(Integer chatId, String message) {
        log.info("modifiedLastChat()");
        String id = "chatId: " + chatId;
        redisTemplate.opsForValue().set(id, message);
    }
}
