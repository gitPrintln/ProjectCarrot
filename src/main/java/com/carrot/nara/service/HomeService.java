package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.PostImage;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeService {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    
    @Transactional(readOnly = true)
    public List<PostImage> recentList(){
        log.info("recentList()");
        List<PostImage> list = new ArrayList<>();
        List<Integer> postIds = postRepository.findRecentPostIds();
        int cnt = 0; // 5개까지만 저장하기 위해서
        for (Integer i : postIds) {
            if(cnt == 5) {
                break;
            }
            Optional<PostImage> entity = Optional.ofNullable(postImageRepository.findFirstByPostId(i));
            if(!entity.isEmpty()) { // 조회했을 경우 이미지가 있는 경우에만 list에 저장하기 위해서.
            list.add(entity.get());
            cnt++;
            }
        }
        for (PostImage p : list) {
            log.info("최신 post 5개{},{},{}", p.getId(), p.getPostId(), p.getOriginFileName());
        }
        return list;
    }
}
