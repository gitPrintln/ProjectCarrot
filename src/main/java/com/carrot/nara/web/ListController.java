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
    
    /**
     * 수정된 시간을 기준으로 내림차순으로 postList를 찾아옴.
     * 그리고 이미지가 있는 경우와 없는 경우로 나눠서 넘겨줄 list에 담아서 준비함.
     * @param model html로 리스트를 구성할 데이터를 전달해줌.
     * @return list.html로 연결
     */
    @GetMapping("")
    public String list(Model model) {
        log.info("list()");
        List<ListReadDto> list = new ArrayList<>();
        
        List<Post> postList = postRepository.findAllByOrderByModifiedTimeDesc();
        for (Post p : postList) {
            Optional<PostImage> pi = Optional.ofNullable(postImageRepository.findFirstByPostId(p.getId()));
            if(pi.isPresent()) { // 이미지가 있을 경우
            PostImage pig = pi.get();
            ListReadDto listElement = ListReadDto.builder().id(p.getId()).imageFileName(pig.getFileName())
                    .imageFilePath(pig.getFilePath()).title(p.getTitle()).region(p.getRegion())
                    .prices(p.getPrices()).modifiedTime(p.getModifiedTime()).build();
            list.add(listElement);
            } else { // 이미지 없는 경우
                ListReadDto listElement = ListReadDto.builder().id(p.getId()).imageFileName("")
                        .imageFilePath("").title(p.getTitle()).region(p.getRegion())
                        .prices(p.getPrices()).modifiedTime(p.getModifiedTime()).build();
                list.add(listElement);
            }
        }
        
        model.addAttribute("list", list);
        return "list";
    }
}
