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
    
    @Transactional(readOnly = true)
    @GetMapping("/idChk")
    @ResponseBody
    public ResponseEntity<Boolean> idChk(@RequestParam String username){
        log.info("idChk(username={})", username);
        Boolean result = userService.checkId(username);
        log.info("result,{}", result);
        return ResponseEntity.ok(result);
    }
    
    @Transactional(readOnly = true)
    @GetMapping("/nicknameChk")
    @ResponseBody
    public ResponseEntity<Boolean> nicknameChk(@RequestParam String nickName){
        log.info("nicknameChk(nickName={})", nickName);
        Boolean result = userService.checkNickName(nickName);
        log.info("result,{}", result);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/signup")
    public String signup(UserRegisterDto dto) {
        log.info("signup(dto={})", dto);
        userService.registerUser(dto);
        return "redirect:/";
    }
    
}
