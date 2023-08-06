package com.carrot.nara.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.dto.UserRegisterDto;
import com.carrot.nara.service.UserService;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/signup")
    public String signUp() {
        log.info("signUp()");
        return "signup";
    }
    
    @GetMapping("/signin")
    public String signIn() {
        log.info("signIn()");
        return "signin";
    }
    
    /**
     * id 중복체크 기능.
     * @param username 중복체크할 id
     * @return 생성가능한 id면 true, 그렇지 않으면 false.
     */
    @Transactional(readOnly = true)
    @GetMapping("/idChk")
    @ResponseBody
    public ResponseEntity<Boolean> idChk(@RequestParam String username){
        log.info("idChk(username={})", username);
        Boolean result = userService.checkId(username);
        log.info("result,{}", result);
        return ResponseEntity.ok(result);
    }
    
    /**
     * nickname 중복체크 기능.
     * @param nickName 중복체크할 nickname
     * @return 생성가능한 nickname이면 true, 그렇지 않으면 false.
     */
    @Transactional(readOnly = true)
    @GetMapping("/nicknameChk")
    @ResponseBody
    public ResponseEntity<Boolean> nicknameChk(@RequestParam String nickName){
        log.info("nicknameChk(nickName={})", nickName);
        Boolean result = userService.checkNickName(nickName);
        log.info("result,{}", result);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 제출버튼을 클릭했을 때 dto를 저장하고 redirect시킴.
     * @param dto 회원가입 완료하게 될 dto
     * @return
     */
    @PostMapping("/signup")
    public String signup(UserRegisterDto dto) {
        log.info("signup(dto={})", dto);
        userService.registerUser(dto); // 비밀번호는 encode()를 통해서 암호화한 뒤 db에 저장.
        return "redirect:/";
    }
    
}
