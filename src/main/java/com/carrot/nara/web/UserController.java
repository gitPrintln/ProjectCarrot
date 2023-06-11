package com.carrot.nara.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    @Autowired
    private HttpSession session;
    
    private final UserService userService;
    
    @GetMapping("/signup")
    public String signUp() {
        log.info("signup()");
        return "signup";
    }
    
    @GetMapping("/signin")
    public String signIn() {
        log.info("signin()");
        return "signin";
    }
    
    @GetMapping("/idChk")
    @ResponseBody
    public ResponseEntity<Boolean> idChk(@RequestParam String username){
        log.info("idChk(username={})", username);
        Boolean result = userService.checkId(username);
        log.info("result,{}", result);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/nicknameChk")
    @ResponseBody
    public ResponseEntity<Boolean> nicknameChk(@RequestParam String nickName){
        log.info("nicknameChk(nickName={})", nickName);
        Boolean result = userService.checkNickName(nickName);
        log.info("result,{}", result);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/signin")
    public String signin() {
        log.info("signin()");
        
        // 이전 페이지의 URL을 세션에 저장
        String previousUrl = (String) session.getAttribute("previousUrl");
        
        if (previousUrl != null) {
            // 이전 페이지로 리다이렉트
            session.removeAttribute("previousUrl");
            return "redirect:" + previousUrl;
        }
        // 이전 페이지가 없을 경우 기본 경로로 리다이렉트
        return "redirect:/";
    }
    
    // 다른 요청에서 이전 페이지의 URL을 저장하기 위한 메서드
    @ModelAttribute
    public void rememberPreviousUrl(HttpServletRequest request) {
        String previousUrl = request.getHeader("Referer");
        
        if (previousUrl != null && !previousUrl.contains("/signin")) {
            session.setAttribute("previousUrl", previousUrl);
        }
    }
}
