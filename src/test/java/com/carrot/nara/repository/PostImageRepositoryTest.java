package com.carrot.nara.repository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.carrot.nara.domain.PostImage;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class PostImageRepositoryTest {

    @Autowired
    PostImageRepository postImageRepository;

//    @Test
//    @Order(1)
    public void testImageSave() {
        PostImage pi = PostImage.builder().fileName("피카피카")
                .filePath("로컬 경로.").originFileName("피카피카이요")
                .postId(1).build();
        postImageRepository.save(pi);
        log.info("{}|{}", pi, pi.getFilePath());
    }
    
//    @Test
//    @Order(2)
    public void testImagePostIdInsert() {
        Integer postId = 41;
        PostImage pi = postImageRepository.findById(1).get();
        log.info("pi:{}", pi);
        // update 후
        pi = pi.update(postId);
        pi = postImageRepository.save(pi);
        log.info("업데이트 후 pi:{}", pi);
    }
    
    @Test
    public void testSelectListImage() {
        Integer postId = 52;
        PostImage p = postImageRepository.findFirstByPostId(postId);
        log.info("p={}",p);
    }
    
    
}
