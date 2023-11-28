package com.carrot.nara.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.carrot.nara.domain.PostImage;
import com.carrot.nara.service.HomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final HomeService homeService;
    
    
    @GetMapping("/")
    public String home(Model model) {
        log.info("home");
        
        List<PostImage> list = homeService.recentList();
        model.addAttribute("mainImages", list);
        
        return "home";
    }
    
    @GetMapping("/map")
    public String map(Model model) {
        log.info("map");
        
        return "map";
    }
}
