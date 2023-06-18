package com.carrot.nara.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.carrot.nara.service.HitsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HitController {

    private final HitsService hitsService;
    
    /**
     * Post글의 조회수 올려주는 기능.
     * id와 username을 통해서 쿠키가 생성되어 있으면 아무 일도 하지 않고 쿠키가 생성되어 있지 않다면 쿠키를 생성하고 조회수를 1올려줌.
     * 쿠키의 이름은 postHitCount, [username_id]의 형태로 저장함.
     * 쿠키 사라지는 시간은 따로 설정가능.
     * @param id 포스트 id
     * @param username 로그인되어있는 id
     * @param request 
     * @param response
     */
    @GetMapping("/postHitCount")
    private void postHitsUp(Integer id, String username, HttpServletRequest request, HttpServletResponse response) {
        log.info("postHitsUp(id={},username={})", id, username);
        if (username == null || username.equals("")) { // 비회원은 전부 anonymoususer로 처리
            username = "anonymoususer";
        }
        Cookie hitCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postHitCount")) {
                    hitCookie = cookie;
                }
            }
        }
        if (hitCookie != null) {
            if (!hitCookie.getValue().contains("[" + username + "_" + id.toString() + "]")) {
                hitsService.postHitsUp(id);
                hitCookie.setValue(hitCookie.getValue() + "_[" + username + "_" + id + "]");
                hitCookie.setPath("/");
                hitCookie.setMaxAge(60 * 30);
                response.addCookie(hitCookie);
            }
        } else {
            hitsService.postHitsUp(id);
            Cookie newCookie = new Cookie("postHitCount","[" + username + "_" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 30);
            response.addCookie(newCookie);
        }
        
    }
}
