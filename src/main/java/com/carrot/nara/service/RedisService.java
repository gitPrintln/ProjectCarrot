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
    private MessageRespository messageRespository; // lastChat 캐시가 없으면 마지막 챗을 가져옴

    // redis에 lastChat 캐시를 설정해둠.
    private void setCacheLastChat(Integer chatId, String lastChat) {
        log.info("cacheLastChat()");
        String id = "lastChat. chatId: " + chatId;
        redisTemplate.opsForValue().set(id, lastChat);
    }

    // redis cache 중 lastChat을 가져옴.
    private String getLastChatFromCache(Integer chatId) {
        log.info("getLastChatFromCache()");
        String id = "lastChat. chatId: " + chatId;
        return redisTemplate.opsForValue().get(id);
    }
    
    /**
     * 1. redis lastChat 캐시 데이터를 확인 후 없으면 2. jpaRepository에서 찾아옴 + lastChat 캐시에 저장.
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

    // lastChat이 바뀌었을 경우. redis lastChat 캐시도 수정해줌.
    public void modifiedLastChat(Integer chatId, String message) {
        log.info("modifiedLastChat()");
        String id = "lastChat. chatId: " + chatId;
        redisTemplate.opsForValue().set(id, message);
    }
    
    // 채팅방에 로그인 되어 있는 지 조회(있으면 true, 없으면 false)
    public boolean isLogInChatRoom(Integer chatId, Integer userId) {
        log.info("loginChatRoom()");
        String ids = "loginuser. chatId: " + chatId + ", userId: " + userId;
        return redisTemplate.hasKey(ids);
    }
    
    // 채팅방에 로그인 중인 유저로 등록. loginuser key값 등록
    public void registerLogInChatRoom(Integer chatId, Integer userId) {
        log.info("registerLoginChatRoom()");
        String ids = "loginuser. chatId: " + chatId + ", userId: " + userId;
        redisTemplate.opsForValue().set(ids, "Log In");
    }
    
    // 채팅방에서 로그아웃 했으므로 loginuser key값 삭제
    public void logOutChatRoom(Integer chatId, Integer userId) {
        log.info("logOutChatRoom()");
        String ids = "loginuser. chatId: " + chatId + ", userId: " + userId;
        redisTemplate.delete(ids);
    }
    
}
