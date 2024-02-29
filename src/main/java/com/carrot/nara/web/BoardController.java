package com.carrot.nara.web;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.domain.Community;
import com.carrot.nara.dto.BoardCreateDto;
import com.carrot.nara.dto.BoardListDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    
    @GetMapping("/notice")
    @Transactional(readOnly = true)
    public String notice(@AuthenticationPrincipal UserSecurityDto userDto, Model model, @RequestParam(defaultValue = "0") int page) {
        log.info("notice()");
        if(userDto != null && userDto.getId() != null) { // 로그인인 경우 user가 작성한 글을 통제할 수 있게 하기 위해 userid를 전달
            model.addAttribute("userid", userDto.getId());
        } else { // 비로그인인 경우 userid가 없음을 나타내기위해 id = -1000을 넣어둠.
            model.addAttribute("userid", -1000); 
        }
        
        String category = "전체공지";
        int pageSize = 3;
        PageRequest pageable = PageRequest.of(page, pageSize);
        Page<Community> list = boardService.getNoticePost(category, pageable);
        
        int startPage = Math.max(0, page - page%5);
        int endPage = Math.min(list.getTotalPages() - 1, page - page%5 + 4);
        model.addAttribute("list", list);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("currentPage", page);
        // 로그인 정보가 있는 경우에는 권한을 가져와보고 관리자인지 유저인지 확인해서 정보를 넘겨준다.
        if(userDto != null && userDto.getId() != null) {
            // 확인을 원하는 권한
            String admin = "ROLE_ADMIN";
            // 사용자 권한을 가져와서 각 역할을 SimpleGrantedAuthority로 변환하고 확인을 원하는 권한을 확인한다.
            // 권한 정보를 SimpleGrantedAuthority로 변환하면 Spring Security가 이를 더 효과적으로 인식하고 처리할 수 있다.
            Collection<GrantedAuthority> roles = userDto.getAuthorities();
            List<SimpleGrantedAuthority> role = roles.stream().map(x -> new SimpleGrantedAuthority(x.getAuthority())).collect(Collectors.toList());
            /*role.stream().filter(s -> s.getAuthority().contains(admin)).
            forEach(x -> System.out.println(userDto.getId() + "'s authority = " + x.getAuthority()));*/
            for (SimpleGrantedAuthority s : role) {
                if(s.getAuthority().contains(admin)) {
                    model.addAttribute("admin", admin);
                }
            }
        }
        return "board/notice";
    }
    
    @GetMapping("/report")
    public String report() {
        log.info("report()");
        return "board/report";
    }
    
    @GetMapping("/cs")
    public String customerService() {
        log.info("customerService()");
        return "board/cs";
    }
    
    // 공지사항 게시판의 전체공지, 자유게시판, FAQ 부분
    @GetMapping("/notice/community/{category}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<BoardListDto> getNoticeCommunity(@PathVariable String category, @RequestParam(defaultValue = "0") int page){
        log.info("getNoticeCommunity()");
        int pageSize = 3;
        PageRequest pageable = PageRequest.of(page, pageSize);
        
        Page<Community> entityList = boardService.getNoticePost(category, pageable);
        int startPage = Math.max(0, page - page%5);
        int endPage = Math.min(entityList.getTotalPages() - 1, page - page%5 + 4);
        
        BoardListDto entity = BoardListDto.builder()
                .category(category)
                .currentPage(page).startPage(startPage).endPage(endPage).totalPages(entityList.getTotalPages())
                .entity(entityList).build();
        
        return ResponseEntity.ok(entity);
    }
    
    /**
     * 전체 공지, 자유게시판, 1:1문의 작성한 내용 DB저장
     * @param userDto 글(공지, 커뮤니티, 문의)를 남기는 유저 정보
     * @param dto 글 정보
     * @return 성공적으로 저장하면 공지사항 게시판으로 이동
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/post")
    @Transactional
    public String boardPost(@AuthenticationPrincipal UserSecurityDto userDto, BoardCreateDto dto) {
        log.info("inquiry(userId={},dto={})", userDto.getId(), dto);
        dto.setUserId(userDto.getId());
        
        // 카테고리가 비어있는 경우는 문의하기 글이므로 문의 카테고리를 넣어줌.
        if(dto.getCategory() == null || dto.getCategory().equals("")) {
            dto.setCategory("문의");
        }
        
        boardService.createBoardPost(dto);
        return "redirect:/board/notice";
    }
}
