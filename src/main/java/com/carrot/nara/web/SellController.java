package com.carrot.nara.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.carrot.nara.dto.PostCreateDto;
import com.carrot.nara.service.SellService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SellController {
    
    private final SellService sellService;
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String sell() {
        log.info("sell()");
        return "sell";
    }
    
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public String create(PostCreateDto dto) {
        log.info("sellCreate(dto={}, {})", dto, dto.getImgIds());
        Integer postId = sellService.create(dto);
        return "redirect:/detail?id=" + postId;
    }
}
