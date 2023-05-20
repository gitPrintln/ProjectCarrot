package com.carrot.nara.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
    @Value("${com.carrot.nara.upload.path}")
    private String uploadPath;
    
    @GetMapping("")
    public String sell() {
        log.info("sell()");
        return "sell";
    }
    
    @PostMapping("/create")
    public String create(PostCreateDto dto) {
        log.info("sellCreate(dto={})", dto);
        sellService.create(dto);
        return "redirect:/";
    }
}
