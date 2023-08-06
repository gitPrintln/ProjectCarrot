package com.carrot.nara.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;
import com.carrot.nara.repository.UserRepository;

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
    
    @Transactional(readOnly = true)
    @GetMapping("")
    public String detail(Integer id, Model model) {
        log.info("detail(id={})", id); // 클릭한 id를 통해서 상세하게 볼 수 있게 정보를 불러옴.
        // # Spring Data JPA(Java Persistence API)
        // spring.jpa.hibernate.ddl-auto=update
        // 으로 설정해둬서 그런지 자꾸만 조회 query를 두 번해서 조회만 하는 부분인데도 자꾸 수정시간을 건드림.
        // 그래서 spring.jpa.hibernate.ddl-auto=validate 로 검증만하고 수정하지 않도록 해둔 뒤
        // 추후에 수정할 일이 있을 때 Flyway나 Liquibase와 같은 데이터베이스 마이그레이션 도구를 사용하면 
        // 안정적이고 효율적인 애플리케이션 운영에 도움 될 것임.
        Post post = postRepository.findById(id).get();
        List<PostImage> postImage = postImageRepository.findByPostId(id);
        String createrNick = userRepository.findById(post.getUserId()).get().getNickName();
        model.addAttribute("createrNick", createrNick);
        model.addAttribute("post", post);
        model.addAttribute("postImage", postImage);
        return "detail";
    }
}
