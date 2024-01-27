package com.carrot.nara.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.nara.domain.Community;
import com.carrot.nara.dto.BoardCreateDto;
import com.carrot.nara.repository.CommunityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    
    private final CommunityRepository communityRepository;
    
    // 공지사항 카테고리에 맞는 DB에서 가져오기
    public List<Community> getNoticePost(String category) {
        log.info("getNoticePost(category={})", category);
        List<Community> entity = communityRepository.findByCategory(category);
        return entity;
    }

    @Transactional
    public void createBoardPost(BoardCreateDto dto) {
        log.info("createBoardPost()");
        communityRepository.save(dto.toEntity());
    }

}
