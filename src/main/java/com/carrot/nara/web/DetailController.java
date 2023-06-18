package com.carrot.nara.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.domain.User;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;
import com.carrot.nara.repository.UserRepository;
import com.carrot.nara.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/detail")
@RequiredArgsConstructor
public class DetailController {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    
    @GetMapping("")
    public String detail(Integer id, Model model) {
        log.info("detail(id={})", id);
        Post post = postRepository.findById(id).get();
        List<PostImage> postImage = postImageRepository.findByPostId(id);
        String createrNick = userRepository.findById(post.getUserId()).get().getNickName();
        
        model.addAttribute("createrNick", createrNick);
        model.addAttribute("post", post);
        model.addAttribute("postImage", postImage);
        return "detail";
    }
}
