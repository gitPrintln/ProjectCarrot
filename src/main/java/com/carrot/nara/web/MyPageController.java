package com.carrot.nara.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.domain.TimeFormatting;
import com.carrot.nara.domain.User;
import com.carrot.nara.dto.ListReadDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.MyPageService;
import com.carrot.nara.service.PostService;
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
    private final PostService postService;
    
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
    @Transactional(readOnly = true)
    public String myItemsList(@AuthenticationPrincipal UserSecurityDto user, Model model) {
        log.info("myItemsList()");
        User u = userService.readById(user.getId());
        model.addAttribute("user", u);
        // type : 0(판매 목록)
        Integer type = 0;
        // 최종 리스트
        List<ListReadDto> list = loadMyList(type, user.getId());
        model.addAttribute("list", list);
        return "mypage/myitems";
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myWishList")
    public String myWishList(@AuthenticationPrincipal UserSecurityDto user, Model model) {
        log.info("myWishList()");
        User u = userService.readById(user.getId());
        model.addAttribute("user", u);
        // type : 1(관심 목록)
        Integer type = 1;
        // 최종 리스트
        List<ListReadDto> list = loadMyList(type, user.getId());
        model.addAttribute("list", list);
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
    
    /**
     * 내 판매/관심 목록(type : 0(판매 목록), 1(관심 목록))에 관한 포스트 글 + 포스트 이미지로 구성된 ListReadDto 타입의 list를 불러옴.
     * @param userId 가져오기를 원하는 user의 id
     * @return ListReadDto 타입의 판매/관심 목록 List를 전달해줌.
     */
    private List<ListReadDto> loadMyList(Integer type, Integer userId){
        log.info("loadMyList()");
        List<ListReadDto> list = new ArrayList<>(); // 최종 리스트
        List<Post> postList = new ArrayList<>(); // 포스트에 대한 리스트(이미지 x) - 최종리스트에 넣기 위해 작업해줘야함. 
        
        if(type == 0) { // type : 0 , 판매 목록인 경우
            postList = postService.readAllMySellList(userId);
        } else if(type == 1) { // type : 1 , 관심 목록인 경우
            postList = postService.readAllMyLikeList(userId);
        }
        
        for (Post p : postList) {
            Optional<PostImage> pi = Optional.ofNullable(postService.readThumbnail(p.getId()));
            String imageFileName = "image-fill.png"; // 포스트 글의 이미지가 없으면 기본 이미지를 넣음.
            String lastModifiedTime = TimeFormatting.formatting(p.getModifiedTime());
            
            if(pi.isPresent()) { // 포스트 글의 이미지가 있으면 있는 이미지로 교체
                PostImage pig = pi.get();
                imageFileName = pig.getFileName();
            }
            ListReadDto listElement = ListReadDto.builder().id(p.getId()).imageFileName(imageFileName)
                    .title(p.getTitle()).region(p.getRegion())
                    .prices(p.getPrices()).chats(p.getChats()).hits(p.getHits()).wishCount(p.getWishCount())
                    .status(p.getStatus())
                    .modifiedTime(lastModifiedTime).build();
            list.add(listElement);
        }
        
        return list;
    }
}
