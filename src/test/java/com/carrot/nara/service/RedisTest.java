package com.carrot.nara.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Service
public class RedisTest {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisService redisService;
    
    @Test
    public void testRedis() {
        // 여기에서 Redis 관련 기능을 테스트하면 됨
//        redisTemplate.opsForValue().set("testKey", "testValue");
//        String value = redisTemplate.opsForValue().get("testKey");
//        System.out.println("Value from Redis: " + value);
        String value = redisService.getDataFromRedis("testKey");
        System.out.println("Value from Redis: " + value);
    }
}
