package com.carrot.nara.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SellController {
    
    @GetMapping("/sell")
    public String sell() {
        return "sell";
    }
}
