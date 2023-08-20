package com.carrot.nara.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.domain.User;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String myPage(@AuthenticationPrincipal UserSecurityDto user, Model model) {
        log.info("myPage()");
        User u = userService.readById(user.getId());
        model.addAttribute("user", u);
        return "mypage";
    }
}
