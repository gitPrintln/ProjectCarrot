package com.carrot.nara.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    @GetMapping("/notice")
    public String notice() {
        log.info("notice()");
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
    
}
