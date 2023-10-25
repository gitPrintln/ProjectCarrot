package com.carrot.nara.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Message;
import com.carrot.nara.domain.PostImage;
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
            // 방만 만들어져있고 채팅 내용이 없을 경우가 있을 수 있으므로
            Optional<Message> message = Optional.ofNullable(messageRespository.findFirstByChatIdOrderByModifiedTimeDesc(chatId));
            if(message.isPresent()) {
                lastChat = message.get().getMessage();
                setCacheLastChat(chatId, lastChat);
            } else {
                lastChat = "";
            }
        }
        return lastChat;
    }

    
    // redis에 lastTime 캐시를 설정해둠.
    private void setCacheLastTime(Integer chatId, String lastTime) {
        log.info("cacheLastTime()");
        String id = "lastTime. chatId: " + chatId;
        redisTemplate.opsForValue().set(id, lastTime);
    }

    // redis cache 중 lastTime을 가져옴.
    private String getLastChatTimeFromCache(Integer chatId) {
        log.info("getLastTimeFromCache()");
        String id = "lastTime. chatId: " + chatId;
        return redisTemplate.opsForValue().get(id);
    }
    
    /**
     * 1. redis lastTime 캐시 데이터를 확인 후 없으면 2. jpaRepository에서 찾아옴 + lastTime 캐시에 저장(String).
     * @param chatId 마지막 시간을 가져올 chatId
     * @return 마지막 시간(LocalDateTime으로 전송-formatting으로 표현하기 위해)
     */
    @Transactional(readOnly = true)
    public LocalDateTime getLastTime(Integer chatId) {
        log.info("getLastTime(id={})", chatId);
        String lastTimeToString = getLastChatTimeFromCache(chatId);
        if(lastTimeToString == null) { 
            Optional<Message> message = Optional.ofNullable(messageRespository.findFirstByChatIdOrderByModifiedTimeDesc(chatId));
            if(message.isPresent()) {
                lastTimeToString = message.get().getModifiedTime().toString();
                setCacheLastTime(chatId, lastTimeToString);
            } else {
                lastTimeToString = "";
            }
        }
        LocalDateTime lastTime = LocalDateTime.of(1111, 11, 11, 11, 11);
        log.info("lllstet{},{}",lastTimeToString,lastTime.toString());
        if(!lastTimeToString.equals("")) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            lastTime = LocalDateTime.parse(lastTimeToString, format);
        } 
        log.info("lllst{},{}",lastTimeToString,lastTime.toString());
        return lastTime;
    }
    // lastChat이 바뀌었을 경우. redis lastChat 캐시도 수정해줌.(+ 시간도 추가)
    // 메세지 내용과 시간으로 같이 한꺼번에 저장하면 관리할 키 개수는 줄어드는 장점이 있지만
    // 유지보수 관리 측면에서 내용과 시간을 나누는 것이 더 나을 것 같다는 생각으로 나눠서 저장.
    public void modifiedLastChat(Integer chatId, String message, LocalDateTime sendTime) {
        log.info("modifiedLastChat()");
        String messageId = "lastChat. chatId: " + chatId;
        String timeId = "lastTime. chatId: " + chatId;
        log.info("lllst{},{},{}", chatId, message, sendTime);
        redisTemplate.opsForValue().set(messageId, message);
        redisTemplate.opsForValue().set(timeId, sendTime.toString().substring(0, 26)); // 2023-10-25T21:20:20.181231 형식의 마지막 나노초 단위에서 6자리로 맞춰야지 나중에 불러올 때도 formatting할 때
                                                                                        // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"); 형식에 맞춰서 불러올 수 있음.
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
