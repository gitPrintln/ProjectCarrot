package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.FileUploadDto;
import com.carrot.nara.dto.PostCreateDto;
import com.carrot.nara.repository.PostImageRepository;
import com.carrot.nara.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    
    @Value("${com.carrot.nara.upload.path}")
    private String uploadPath;
    
    @Transactional
    public Integer create(PostCreateDto dto) {
        log.info("create(dto={})", dto);
        Integer postId = postRepository.save(dto.toEntity(dto.getUserId())).getId();
        
        if(dto.getImgIds() != null && !dto.getImgIds().isEmpty()) {
        for (Integer n : dto.getImgIds()) {
            PostImage entity = postImageRepository.findById(n).get();
            entity.update(postId);
        }
        }
        return postId;
    }
    
    @Transactional
    public List<Integer> createImg(String[] files) {
        log.info("createImg()");
        for (String f : files) {
            log.info("files={}", f);
        }
        List<Integer> imgIds = new ArrayList<>();
        for (String file : files) {
            String originFileName = file.split("_")[1];
            PostImage entity = PostImage.builder().fileName(file).filePath(uploadPath + file)
                    .originFileName(originFileName).postId(null).build();
            postImageRepository.save(entity);
            imgIds.add(entity.getId());
        }
        
        return imgIds;
    }
    
}
