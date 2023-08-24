package com.carrot.nara.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.PostCreateDto;
import com.carrot.nara.dto.PostModifyDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.PostService;
import com.carrot.nara.service.SellService;
import com.carrot.nara.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SellController {
    
    private final SellService sellService;
    private final PostService postService;
    private final UserService userService;
    
    // 판매 상품 등록
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String sell() {
        log.info("sell()");
        return "sell/sell";
    }
    
    // 판매 상품 등록 DB에 저장
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserSecurityDto userDto, PostCreateDto dto) {
        dto.setUserId(userDto.getId());
        log.info("sellCreate(dto={}, {})", dto, dto.getImgIds());
        Integer postId = sellService.create(dto);
        return "redirect:/sell/detail?id=" + postId;
    }
    
    // 판매 상품 상세 보기
    @GetMapping("/detail")
    @Transactional(readOnly = true)
    public String detail(Integer id, Model model) {
        log.info("sellDetail(id={})", id); // 클릭한 id를 통해서 상세하게 볼 수 있게 정보를 불러옴.
        // # Spring Data JPA(Java Persistence API)
        // spring.jpa.hibernate.ddl-auto=update
        // 으로 설정해둬서 그런지 자꾸만 조회 query를 두 번해서 조회만 하는 부분인데도 자꾸 수정시간을 건드림.
        // 그래서 spring.jpa.hibernate.ddl-auto=validate 로 검증만하고 수정하지 않도록 해둔 뒤
        // 추후에 수정할 일이 있을 때 Flyway나 Liquibase와 같은 데이터베이스 마이그레이션 도구를 사용하면 
        // 안정적이고 효율적인 애플리케이션 운영에 도움 될 것임.
        /* dirty checking(상태 변경 검사) - Dirty란 상태의 변화가 생긴 정도
         * 트랜잭션이 끝나는 시점에 변화가 있는 모든 엔티티 객체를 데이터베이스에 자동으로 반영
         * 해결
         * 1)equals를 재정의 하지 않은 것 
         * 2)OSIV(Open-Session-In-View)를 OFF하지 않은 것
         * 근데 dirty checking 에서 @LastModifiedDate, @OneToOne, @ManyToOne은 제외되는데..?
         */
        Post post = postService.readByPostId(id);
        log.info("postDetail(post={},{})", post.toString(), post.getModifiedTime());
        model.addAttribute("post", post);
        
        List<PostImage> postImage = postService.readImgsByPostId(id);
        model.addAttribute("postImage", postImage);

        String createrNick = userService.getNickName(post.getUserId());
        model.addAttribute("createrNick", createrNick);
        return "sell/detail";
    }
    
    // 판매 상품 수정
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/modify")
    public String modify(@AuthenticationPrincipal UserSecurityDto userDto, Integer postId, Model model) {
        log.info("sellModify(postId={})", postId);
        Post post = postService.readByPostId(postId);
        model.addAttribute("post", post);
        List<PostImage> postImages = postService.readImgsByPostId(postId);
        model.addAttribute("postImage", postImages);
        
        // 주소 분리 작업
        if(post.getRegion() != null) { // 주소가 있을 때만 실행
            String[] splitRegion = post.getRegion().split(",");
            String regionMain = "";
            String detailRegion = "";
            if(splitRegion.length>1) { // 세부 주소가 있으면 세부주소까지 담아주고 없으면 그냥 메인 주소만 담아서 보냄
                regionMain = splitRegion[0];
                detailRegion = splitRegion[1];
            } else {
                regionMain = splitRegion[0];
            }
            model.addAttribute("regionMain", regionMain);
            model.addAttribute("detailRegion", detailRegion);
        }
        return "sell/modify";
    }
    
    // 판매 상품 수정 DB에 저장
    @Transactional
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/modify")
    public String update(@AuthenticationPrincipal UserSecurityDto userDto, PostModifyDto dto) {
        log.info("sellUpdate(dto={}, {})", dto, dto.getImgIds());
        Integer postId = sellService.modify(dto);
        return "redirect:/sell/detail?id=" + postId;
    }
    
    // 포스트 글과 그 글의 이미지, 로컬저장소의 이미지, 관련 채팅방 모두 삭제
    @Transactional
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserSecurityDto userDto, Integer id) {
        log.info("sellUpdate(postID={})", id);
        sellService.deletePost(id);
        return "redirect:/list";
    }
}
