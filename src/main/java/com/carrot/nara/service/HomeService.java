package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
    
    public List<PostImage> recentList(){
        log.info("recentList()");
        List<PostImage> list = new ArrayList<>();
        List<Integer> postIds = postRepository.findRecentPostIds();
        int cnt = 0;
        for (Integer i : postIds) {
            if(cnt == 5) {
                break;
            }
            PostImage entity = postImageRepository.findFirstByPostId(i);
            list.add(entity);
            cnt++;
        }
        return list;
    }
}
