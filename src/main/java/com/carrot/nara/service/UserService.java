package com.carrot.nara.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.User;
import com.carrot.nara.dto.UserRegisterDto;
import com.carrot.nara.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Boolean checkId(String username) {
        log.info("checkId(username={})",username);
        boolean result = userRepository.findByUsername(username).isEmpty();
        return result;
    }

    @Transactional(readOnly = true)
    public Boolean checkNickName(String nickName) {
        log.info("checkNickName(nickName={})",nickName);
        boolean result = userRepository.findByNickName(nickName).isEmpty();
        return result;
    }
    
    /**
     * 회원가입 정보를 DB에 저장
     * @param dto 저장할 유저 정보
     */
    public void registerUser(UserRegisterDto dto) {
        log.info("registerUser()");
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(dto.toEntity());
    }

    /**
     * 프로필 이미지 가져오기
     * @param userId 가져올 userId
     * @return 저장되어 있는 유저 프로필 이미지
     */
    @Transactional(readOnly = true)
    public String getImageName(Integer userId) {
        log.info("getImageName()");
        return userRepository.findById(userId).get().getUserImage();
    }

    /**
     * 유저 닉네임 가져오기
     * @param userId 가져올 userId
     * @return DB에 있는 유저 닉네임
     */
    @Transactional(readOnly = true)
    public String getNickName(Integer userId) {
        log.info("getNickName()");
        return userRepository.findById(userId).get().getNickName();
    }

    /**
     * 유저 정보 불러오기
     * @param id 불러올 userId
     * @return DB에 있는 유저 정보
     */
    public User readById(Integer id) {
        log.info("readById(id={})", id);
        return userRepository.findById(id).get();
    }

}
