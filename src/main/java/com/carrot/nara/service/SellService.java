package com.carrot.nara.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.carrot.nara.domain.Chat;
import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.PostCreateDto;
import com.carrot.nara.dto.PostModifyDto;
import com.carrot.nara.repository.ChatRepository;
import com.carrot.nara.repository.MessageRespository;
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
    private final ChatRepository chatRepository;
    private final MessageRespository messageRespository;
    
    
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

    // 판매 포스트 글 수정
    @Transactional
    public Integer modify(PostModifyDto dto) {
        log.info("modify(dto={})", dto);
        Integer id = dto.getId();
        Post post = postRepository.findById(id).get();
        post.update(dto.toEntity(id));
        
        if(dto.getImgIds() != null && !dto.getImgIds().isEmpty()) {
            for (Integer n : dto.getImgIds()) {
                PostImage entity = postImageRepository.findById(n).get();
                entity.update(id);
            }
        }
        return id;
    }
    
    // 파일네임에 해당하는 포스트이미지 삭제
    @Transactional
    public void deleteImg(String fileName) {
        log.info("deleteImg(fileName={})", fileName);
        postImageRepository.deleteByFileName(fileName);
    }

    // 해당 ID의 포스트 글 관련 이미지, 채팅 삭제
    public void deletePost(Integer id) {
        log.info("deletePost(id={})", id);
        postRepository.deleteById(id); // 포스트 글 삭제
        
        List<PostImage> imageListForDelete = postImageRepository.findByPostId(id); // 로컬에서 삭제할 이미지 이름 찾아오기
        for (PostImage f : imageListForDelete) {
            File file = new File(uploadPath, f.getFileName());
            file.delete(); // 로컬에서 파일 삭제
        }
        
        postImageRepository.deleteByPostId(id); // 포스트 글에 딸린 이미지 삭제
        
        List<Chat> chatListForDelete = chatRepository.findByPostId(id); // postId에 해당하는 삭제할 채팅방 찾아오기
        for (Chat c : chatListForDelete) {
            messageRespository.deleteByChatId(c.getId()); // 해당 chatID의 메세지 모두 삭제
        }
        chatRepository.deleteByPostId(id); // 해당 postID의 채팅방 모두 삭제
    }

    // 포스트글의 status를 수정함.
    @Transactional
    public void modifyStatus(Integer postId, String status) {
        log.info("modifyStatus(id={},status={})", postId, status);
        postRepository.updateStatus(postId, status);
    }
}
