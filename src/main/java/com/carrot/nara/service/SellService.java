package com.carrot.nara.service;

import java.util.Iterator;
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
    public void create(PostCreateDto dto) {
        log.info("create(dto={})", dto);
        // TODO: USER 만들어지면 USER ID 넣을 것.(createDTO도 수정) -> 임시로 userID : 1
        postRepository.save(dto.toEntity(1));
    }
    
    @Transactional
    public void createImg(List<FileUploadDto> files) {
        log.info("createImg(files={})", files);
        for (FileUploadDto f : files) {
            String originFileName = f.getFileName().split("_")[1];
            PostImage entity = PostImage.builder().fileName(f.getFileName()).filePath(uploadPath + f.getFileName())
                    .originFileName(originFileName).postId(null).build();
            postImageRepository.save(entity);
        }
    }
    
}
