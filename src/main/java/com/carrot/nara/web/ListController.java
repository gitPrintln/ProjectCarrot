package com.carrot.nara.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.ListReadDto;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/list")
@RequiredArgsConstructor
public class ListController {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    
    @GetMapping("")
    public String list(Model model) {
        log.info("list()");
        List<ListReadDto> list = new ArrayList<>();
        
        List<Post> postList = postRepository.findAll();
        for (Post p : postList) {
            Optional<PostImage> pi = Optional.ofNullable(postImageRepository.findFirstByPostId(p.getId()));
            if(pi.isPresent()) {
            PostImage pig = pi.get();
            ListReadDto listElement = ListReadDto.builder().imageFileName(pig.getFileName())
                    .imageFilePath(pig.getFilePath()).title(p.getTitle()).region(p.getRegion())
                    .prices(p.getPrices()).modifiedTime(p.getModifiedTime()).build();
            list.add(listElement);
            } else {
                ListReadDto listElement = ListReadDto.builder().imageFileName("빈")
                        .imageFilePath("빈").title(p.getTitle()).region(p.getRegion())
                        .prices(p.getPrices()).modifiedTime(p.getModifiedTime()).build();
                list.add(listElement);
            }
        }
        
        model.addAttribute("list", list);
        return "list";
    }
}
