package com.carrot.nara.service;

import org.springframework.stereotype.Service;

import com.carrot.nara.dto.PostCreateDto;
import com.carrot.nara.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellService {

    private final PostRepository postRepository;
    
    @Transactional
    public void create(PostCreateDto dto) {
        log.info("create(dto={})", dto);
        // TODO: USER 만들어지면 USER ID 넣을 것.(createDTO도 수정) -> 임시로 userID : 1
        postRepository.save(dto.toEntity(1));
    }
}
