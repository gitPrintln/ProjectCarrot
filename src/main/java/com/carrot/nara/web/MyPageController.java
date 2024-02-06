package com.carrot.nara.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.domain.User;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.MyPageService;
import com.carrot.nara.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;
    private final MyPageService myPageService;
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String myPage(@AuthenticationPrincipal UserSecurityDto user, Model model) {
        log.info("myPage()");
        User u = userService.readById(user.getId());
        model.addAttribute("user", u);
        return "mypage/mypage";
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myItemsList")
    public String myItemsList(@AuthenticationPrincipal UserSecurityDto user, Model model) {
        log.info("myItemsList()");
        User u = userService.readById(user.getId());
        model.addAttribute("user", u);
        return "mypage/myitems";
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myWishList")
    public String myWishList(@AuthenticationPrincipal UserSecurityDto user, Model model) {
        log.info("myWishList()");
        User u = userService.readById(user.getId());
        model.addAttribute("user", u);
        return "mypage/mywish";
    }
    
    
    /**
     * user가 누른 좋아요를 DB에 반영, 해당 post글의 관심수 1올려줌
     * @param postId post글
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @Transactional
    @GetMapping("/postLike")
    @ResponseBody
    public ResponseEntity<String> postLike(@AuthenticationPrincipal UserSecurityDto user, Integer postId){
        log.info("postLike(userId={}, postId={})", user.getId(), postId);
        String likeStatus = myPageService.likeStatusChg(user.getId(), postId);
        return ResponseEntity.ok(likeStatus);
    }
}
