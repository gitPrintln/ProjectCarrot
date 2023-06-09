package com.carrot.nara.service;

import org.springframework.stereotype.Service;

import com.carrot.nara.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    public Boolean checkId(String username) {
        log.info("checkId(username={})",username);
        boolean result = userRepository.findByUsername(username).isEmpty();
        return result;
    }

    public Boolean checkNickName(String nickName) {
        log.info("checkNickName(nickName={})",nickName);
        boolean result = userRepository.findByNickName(nickName).isEmpty();
        return result;
    }

}
