package com.carrot.nara.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {
        // 로그인 성공 시 수행할 로직 추가
        HttpSession session = request.getSession();
        String prevPage = request.getParameter("prevPage");
        log.info("0prev?={}", prevPage);
        if (prevPage != null && !prevPage.isEmpty()) {
            session.setAttribute("prevPage", prevPage);
        }
        
        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("target url?={}", targetUrl);
        // 수동으로 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 이전 페이지의 URL을 가져와 사용하거나 기본 URL로 설정
        String prevPage = (String) request.getSession().getAttribute("prevPage");
        log.info("1prev?={}", prevPage);
        if (prevPage != null && !prevPage.isEmpty()) {
            request.getSession().removeAttribute("prevPage");
            return prevPage;
        }
        log.info("2prev?={}", prevPage);
        return "/";
    }
}
