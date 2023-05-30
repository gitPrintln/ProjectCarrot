package com.carrot.nara.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.domain.Post;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/detail")
@RequiredArgsConstructor
public class DetailController {

    private final PostRepository postRepository;
    
    @GetMapping("")
    public String detail(Integer id, Model model) {
        log.info("detail(id={})", id);
        Post post = postRepository.findById(id).get();
        model.addAttribute("post", post);
        return "detail";
    }
}
