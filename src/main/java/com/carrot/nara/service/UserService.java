package com.carrot.nara.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.User;
import com.carrot.nara.dto.UserRegisterDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.dto.UserUpdateDto;
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
    @Transactional
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
    @Transactional(readOnly = true)
    public User readById(Integer id) {
        log.info("readById(id={})", id);
        return userRepository.findById(id).get();
    }

    /**
     * 사용자 본인임을 확인하기 위해 비밀번호 일치를 확인
     * @param password DB에 저장된 비밀번호
     * @param nowPw 사용자가 입력한 비밀번호
     * @return 일치하면 true, 실패하면 false
     */
    public Boolean isMatchPassword(String inputPassword, String savedPassword) {
        log.info("isMatchPassword(inputPassword={},savedPassword={})", inputPassword, savedPassword);
        Boolean result = false;
        result = passwordEncoder.matches(inputPassword, savedPassword);
        return result;
    }
    
    /**
     * 사용자의 현재 비밀번호를 새로운 비밀번호로 업데이트함.
     * @param userId 업데이트할 사용자의 id
     * @param newPw 새로운 비밀번호
     */
    @Transactional
    public void updatePassword(Integer userId, String newPw) {
        log.info("updatePassword(userId={},newPw={})", userId, newPw);
        User user = userRepository.findById(userId).get();
        user.updatePassword(passwordEncoder.encode(newPw));
    }

    /**
     * 사용자의 현재 프로필 이미지를 새로운 프로필 이미지로 업데이트함.
     * @param userId 업데이트할 사용자의 id
     * @param imageFileName 새로운 프로필 이미지
     */
    @Transactional
    public void saveUserProfileImage(Integer userId, String imageFileName) {
        log.info("saveUserProfileImage(userId={},imageFileName={})", userId, imageFileName);
        User user = userRepository.findById(userId).get();
        user.updateUserImage(imageFileName);
    }

    /**
     * 사용자의 닉네임, 폰번호, 이메일, 지역을 수정함.
     * @param id 업데이트할 사용자의 id
     * @param dto 닉네임, 폰번호, 이메일, 지역
     */
    @Transactional
    public void updateUserInfo(Integer id, UserUpdateDto dto) {
        log.info("updateUserInfo()");
        User user = userRepository.findById(id).get();
        user.updateUserInfo(dto);
    }

}
