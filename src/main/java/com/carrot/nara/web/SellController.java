package com.carrot.nara.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/sell")
public class SellController {
    
    @GetMapping("")
    public String sell() {
        return "sell";
    }
    
    @PostMapping("/create")
    public String create() {
        return "home";
    }
}
