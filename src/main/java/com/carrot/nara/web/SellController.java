package com.carrot.nara.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.dto.PostCreateDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sell")
public class SellController {
    
    @GetMapping("")
    public String sell() {
        log.info("sell()");
        return "sell";
    }
    
    @PostMapping("/create")
    public String create(PostCreateDto dto) {
        log.info("sellCreate() {}", dto.toString());
        return "home";
    }
}
