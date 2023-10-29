package com.carrot.nara.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.domain.TimeFormatting;
import com.carrot.nara.dto.ListReadDto;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;
import com.carrot.nara.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/list")
@RequiredArgsConstructor
public class ListController {

    private final PostService postService;
    
    /**
     * 수정된 시간을 기준으로 내림차순으로 postList를 찾아옴.
     * 그리고 이미지가 있는 경우와 없는 경우로 나눠서 넘겨줄 list에 담아서 준비함.
     * @param model html로 리스트를 구성할 데이터를 전달해줌.
     * @return list.html로 연결
     */
    @Transactional(readOnly = true)
    @GetMapping("")
    public String list(Model model) {
        log.info("list()");
        String keyword = ""; // keyword 없이 전체 목록 불러올 때
        List<ListReadDto> list = loadList(keyword);
        model.addAttribute("list", list);
        
        return "list";
    }
    
    /**
     * 검색 키워드에 해당하는 결과 값을, 수정된 시간을 기준으로 내림차순으로 postList를 찾아옴.
     * @param keyword 검색 키워드
     * @param model html로 리스트를 구성할 데이터를 전달해줌.
     * @return list.html로 연결
     */
    @Transactional(readOnly = true)
    @GetMapping("/search")
    public String listSearch(String keyword, Model model) {
        log.info("list()");
        List<ListReadDto> list = loadList(keyword);
        model.addAttribute("list", list);
        
        return "list";
    }
    
    /**
     * 해당 포스트 글 + 포스트 이미지로 구성된 ListReadDto 타입의 list를 불러옴.
     * @param keyword 검색 키워드가 있을 경우의 keyword, 전체 리스트인 경우 null로 전달
     * @return ListReadDto 타입의 List를 전달해줌.
     */
    private List<ListReadDto> loadList(String keyword){
        log.info("loadList()");
        List<ListReadDto> list = new ArrayList<>(); // 최종 리스트
        List<Post> postList = new ArrayList<>(); // 포스트에 대한 리스트(이미지 x) - 최종리스트에 넣기 위해 작업해줘야함. 
        
        if(!keyword.equals("")) { // 검색 키워드로 리스트 불러올 때(상단의 검색창으로 검색시)
            postList = postService.readByKeywordByUpdateTime(keyword);
        } else { // 전체 리스트 불러올 때(상단의 중고거래 목록 보기 클릭시)
            postList = postService.readAllByUpdateTime();
        }
        
        for (Post p : postList) {
            // 이미지가 있을 수도 있고 없을 수도 있기 때문에 optional로 조회 후 optional로 감싸서 객체를 생성
            // 주의, 전달된 값이 null이라면 Optional.empty()를 반환함.
            Optional<PostImage> pi = Optional.ofNullable(postService.readThumbnail(p.getId()));
            String imageFileName = "image-fill.png"; // 포스트 글의 이미지가 없으면 기본 이미지를 넣음.
            String lastModifiedTime = TimeFormatting.formatting(p.getModifiedTime());
            
            if(pi.isPresent()) { // 포스트 글의 이미지가 있으면 있는 이미지로 교체
                PostImage pig = pi.get();
                imageFileName = pig.getFileName();
            }
            ListReadDto listElement = ListReadDto.builder().id(p.getId()).imageFileName(imageFileName)
                    .title(p.getTitle()).region(p.getRegion())
                    .prices(p.getPrices()).chats(p.getChats()).hits(p.getHits()).wishCount(p.getWishCount())
                    .status(p.getStatus())
                    .modifiedTime(lastModifiedTime).build();
            list.add(listElement);
        }
        
        return list;
    }
}
