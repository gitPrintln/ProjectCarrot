package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.carrot.nara.domain.PostImage;
import com.carrot.nara.repository.PostImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeService {

    private final PostImageRepository postImageRepository;
    
    public List<PostImage> recentList(){
        log.info("recentList()");
        List<PostImage> list = new ArrayList<>();
        list = postImageRepository.findTop5ByOrderByIdDesc();
        
        return list;
    }
}
