package com.carrot.nara.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    
    /**
     * 카테고리에 해당하는 게시판 리스트를 불러옴.
     * @param category 불러올 카테고리
     * @return community 타입의 리스트
     */
    @Transactional(readOnly = true)
    public Page<Community> getNoticePost(String category, PageRequest pageable) {
        log.info("getNoticePost(category={})", category);
        Page<Community> entity = communityRepository.findByCategory(category, pageable);
        return entity;
    }

    @Transactional
    public void createBoardPost(BoardCreateDto dto) {
        log.info("createBoardPost()");
        communityRepository.save(dto.toEntity());
    }


}
