package com.carrot.nara.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String chat() {
        log.info("chat()");
        return "chat";
    }
    
}
