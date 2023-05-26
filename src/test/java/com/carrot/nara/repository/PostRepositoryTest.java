package com.carrot.nara.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.PostUpdateDto;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class PostRepositoryTest {
    
    @Autowired
    private PostRepository postRepository;
    
//    @Test
    public void testSave() {
        // 입력값을 통해 DB에 저장이 되는지 확인
//          Post p1 = Post.builder().title("제목1").category("장난감").content("내용2").prices("13,321").userId(1).build();
//        postRepository.save(p1);
        
        // 업데이트 시간 확인
        // 2번 id에 해당하는 데이터가 있는지 찾아 p1에 저장.
        Post p1 = postRepository.findById(1).get();
        log.info("생성 확인{} | {} | {}", p1, p1.getCreatedTime(), p1.getModifiedTime());
        // p1.update("바뀐 제목입니다!!"); title을 update(String title)에 집어넣어서 변경해보기
        Post p2 = postRepository.save(p1);
        log.info("저장 확인{} | {} | {}", p2, p2.getCreatedTime(), p2.getModifiedTime());
        
    }
    
}
