package com.carrot.nara.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.FileUploadDto;
import com.carrot.nara.dto.PostCreateDto;
import com.carrot.nara.dto.PostModifyDto;
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
    
    // DB에 save하면 id가 생길 테고 그 생긴 id를 postimage에서도 저장된 이미지에 저장해둬야 나중에 불러옴.
    // 이미지가 없다면 넘어감.
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
    
    // 글작성을 했을 경우 중간 과정 중 하나인데 이미지를 postimage table에 저장함.
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

    // post글 수정기능
    @Transactional
    public Integer modify(PostModifyDto dto) {
        log.info("modify(dto={})", dto);
        Integer id = dto.getId();
        Post post = postRepository.findById(id).get();
        post.update(dto.toEntity(id));
        return id;
    }
}
