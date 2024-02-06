package com.carrot.nara.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.dto.PwUpdateDto;
import com.carrot.nara.dto.UserRegisterDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.dto.UserUpdateDto;
import com.carrot.nara.service.UserService;

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
        /*if (prevPage != null) {
            model.addAttribute("prevPage", prevPage);
        }*/
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
    
    /**
     * 본인 인증 확인 이후, 인증에 성공하면 비밀 번호를 변경함
     * @param dto user에 대한정보
     * @param nowPw 사용자가 입력한 인증을 위한 비밀번호
     * @param newPw 사용자가 사용할 새로운 비밀번호
     * @return 성공하면 true, 실패하면 false
     */
    /* @ResponseBody는 Spring MVC 컨트롤러의 메서드가 HTTP 응답의 본문(body) 데이터를 직접 생성하도록 지정
     *                 컨트롤러의 메서드가 반환하는 객체나 데이터가 HTTP 응답으로 변환되어 전달
     *                 주로 JSON이나 XML과 같은 데이터 포맷으로 응답을 생성할 때 사용
    */                 
    // @RequestParam은 URL 파라미터를 처리하는데 사용, 객체 전체를 받아들이려면 @RequestBody를 사용
    // 객체 전체를 받을 때 Dto를 만들어서 받는 것이 일반적임(가독성과 유지보수 측면 좋음)
    @PostMapping("/pwUpdate")
    @ResponseBody
    public ResponseEntity<Boolean> pwUpdate(@AuthenticationPrincipal UserSecurityDto dto,
                                            @RequestBody PwUpdateDto pwInfo){
        String nowPw = pwInfo.getNowPw();
        String newPw = pwInfo.getNewPw();
        
        log.info("pwUpdate(nowpw={},newpw={})", nowPw, newPw);
        Boolean result = false; // 비밀번호 변경이 성공적으로 완료하면 true, 실패하면 false
        
        // 본인 인증이 확인이 완료되면 true, 실패하면 false
        Boolean identityVerification = userService.isMatchPassword(nowPw, dto.getPassword());
        if(!identityVerification) { // 현재 비밀번호를 통해 본인인증에 실패한 경우
            log.info("현재 비밀번호 일치 x");
            return ResponseEntity.ok(result);
        }
        Boolean samePassword = userService.isMatchPassword(newPw, dto.getPassword());
        if(samePassword) { // 바꾸려는 비밀번호가 현재 비밀번호랑 같은 비밀번호일때
            log.info("현재 비밀번호와 새로운 비밀번호 같음");
            return ResponseEntity.ok(result);
        }
        
        userService.updatePassword(dto.getId(), newPw);
        result = true;
        return ResponseEntity.ok(result);
    }
    
    /**
     * 수정 완료버튼을 클릭했을 때 유저 정보를 수정하고, 다시 로그인시킴.
     * @param dto 수정할 유저 정보
     * @return
     */
    @PostMapping("/update")
    public String userUpdate(@AuthenticationPrincipal UserSecurityDto user, UserUpdateDto dto) {
        log.info("userUpdate(user={}, dto={})",user, dto);
        userService.updateUserInfo(user.getId(), dto);
        return "redirect:/logout";
    }
    
    /**
     * 공지사항 기능에서 내 문의 내역, 문의남기기, 글 작성하기 기능은 로그인 유저만 이용할 수 있도록 하기 위함
     * 유저가 로그인되어있는 상태인지 아닌 상태인지 체크하기 위한 함수
     * @param user 체크하려는 유저
     * @return 로그인 정보가 있으면, true 없으면 false 반환
     */
    @PostMapping("/loggedInChk")
    @ResponseBody
    public ResponseEntity<Boolean> userLoggedInChk(@AuthenticationPrincipal UserSecurityDto user){
        log.info("isUserLoggedIn(user={}, userId={})",user);
        boolean isUserLoggedIn = false;
        if(user != null && user.getId() != null) {
            isUserLoggedIn = true;
        }
        return ResponseEntity.ok(isUserLoggedIn);
    }
}
