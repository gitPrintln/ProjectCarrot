package com.carrot.nara.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String notice(Model model, @RequestParam(defaultValue = "0") int page) {
        log.info("notice()");
        String category = "전체공지";
        PageRequest pageable = PageRequest.of(page, 3);
        Page<Community> list = boardService.getNoticePost(category, pageable);
        
        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(list.getTotalPages() - 1, page + 2);
        model.addAttribute("list", list);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("currentPage", page);
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
    public ResponseEntity<Page<Community>> getNoticeCommunity(@PathVariable String category, @RequestParam(defaultValue = "0") int page){
        log.info("getNoticeCommunity()");
        PageRequest pageable = PageRequest.of(page, 3);
        Page<Community> entity = boardService.getNoticePost(category, pageable);
        return ResponseEntity.ok(entity);
    }
    
    /**
     * 전체 공지, 자유게시판, 1:1문의 DB저장
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
