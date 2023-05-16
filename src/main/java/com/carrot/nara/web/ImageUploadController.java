package com.carrot.nara.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/img")
public class ImageUploadController {
    
    @Value("${com.carrot.nara.upload.path}")
    private String uploadPath;
    
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> currentImg(@PathVariable String fileName){
        log.info("currentImg({})", fileName);
        
        File file = new File(uploadPath, fileName); // 로컬 저장소(파일 저장 경로)와, 파일이름으로 File 타입 file을 생성
        
        String contentType = null; // 파일의 MIME 유형(contentType)을 저장하기 위한 변수
        try {
            contentType = Files.probeContentType(file.toPath()); // probeContentType로 파일 유형 확인, 파일 확장자를 기반으로 MIME 유형 결정
        } catch (IOException e) {
            log.error("{} : {}", e.getCause(), e.getMessage()); // 오류 로그 기록
            return ResponseEntity.internalServerError().build(); // 서버로 오류 응답
        }
        
        
        HttpHeaders headers = new HttpHeaders(); // HTTP 응답 헤더를 설정하기 위해
        headers.add("Content-Type", contentType); // Content-Type 헤더 필드를 추가
        
        Resource resource = new FileSystemResource(file); // file 객체를 FileSystemResource를 통해 읽을 수 있도록 resource 타입 객체로 생성
        
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
