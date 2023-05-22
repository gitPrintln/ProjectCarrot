package com.carrot.nara.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carrot.nara.dto.FileUploadDto;
import com.carrot.nara.dto.ImageUploadDto;
import com.carrot.nara.service.SellService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/img")
public class ImageUploadController {
    
    private final SellService sellService;
    
    @Value("${com.carrot.nara.upload.path}")
    private String uploadPath; // application.properties 파일에 설정된 값을 읽어서 변수에 할당.
    
    // 이미지 파일 로컬 저장소에 저장하기
    @PostMapping("/upload")
    public ResponseEntity<List<FileUploadDto>> uploadImg(ImageUploadDto dto){
        log.info("uploadImg(dto={})", dto);
        List<MultipartFile> files = dto.getFiles();
        if(files == null) {
            return ResponseEntity.noContent().build(); //응답 본문이 없는 경우, 상태 코드 204 No Content를 포함한 빈 응답을 생성
        }
        List<FileUploadDto> list = new ArrayList<>();
        files.forEach(multipartFile -> {
            log.info(multipartFile.getOriginalFilename());
            log.info(multipartFile.getContentType());
            log.info("size = {}", multipartFile.getSize());
            FileUploadDto result = saveImg(multipartFile);
            list.add(result);
        });
        
        // DB에 이미지 정보(경로, 이름, postId) 저장.
        sellService.createImg(list);
        
        return ResponseEntity.ok(list);
        
    }
    
    // UUID 클래스를 통해서 파일 저장하기 위해 만든 메서드
    // UUID(Universally Unique Identifier)
    // 무작위로 생성되므로 각 실행마다 다른 값이 생성
    // MultipartFile : 웹 애플리케이션에서 파일 업로드를 처리하기 위해 사용되는 객체
    // |__ 이 객체를 통해 파일의 원본 이름, 크기, MIME 타입 등과 같은 정보에 접근 가능. 또한, 파일 데이터 자체에 접근하여 파일을 읽고 저장 가능
    // |__ Spring의 MultipartFile 구현체인 CommonsMultipartFile은 업로드된 파일을 처리하는 데 사용되며, 더 많은 메서드를 제공함.
    private FileUploadDto saveImg(MultipartFile file) {
        log.info("saveImg(file={})", file);
        FileUploadDto result = null;
        
        String originName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString(); 
        // UUID라는 클래스로 유일한 식별자 만들어주기(중복된 이름 제거)
        // 총 36개 문자(32개 문자와 4개의 하이픈, 128bit)로 된 8-4-4-4-12라는 5개의 그룹을 하이픈으로 구분
        // 다만 확률이 존재한다는 점에서 "만약에"라는 가정에서 자유롭지 못함. 그래서 UUID에 다른 문자열을 추가하여 사용
        boolean image = false;
        String finalFileName = uuid + "_" + originName;
        log.info(finalFileName);
        
        File finalDest = new File(uploadPath, finalFileName); // 새로운 파일을 저장하기 위해
        try {
            file.transferTo(finalDest);
            
            result = FileUploadDto.builder()
                    .uuid(uuid)
                    .fileName(finalFileName)
                    .image(image)
                    .build();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    
//    // 로컬에 저장되어 있는 이미지 파일을 보려고 할 때
//    @GetMapping("/view/{fileName}")
//    public ResponseEntity<Resource> currentImg(@PathVariable String fileName){
//        log.info("currentImg({})", fileName);
//        
//        File file = new File(uploadPath, fileName); // 로컬 저장소(파일 저장 경로)와, 파일이름으로 File 타입 file을 생성
//        
//        String contentType = null; // 파일의 MIME 유형(contentType)을 저장하기 위한 변수
//        try {
//            contentType = Files.probeContentType(file.toPath()); // probeContentType로 파일 유형 확인, 파일 확장자를 기반으로 MIME 유형 결정
//        } catch (IOException e) {
//            log.error("{} : {}", e.getCause(), e.getMessage()); // 오류 로그 기록
//            return ResponseEntity.internalServerError().build(); // 서버로 오류 응답
//        }
//        
//        
//        HttpHeaders headers = new HttpHeaders(); // HTTP 응답 헤더를 설정하기 위해
//        headers.add("Content-Type", contentType); // Content-Type 헤더 필드를 추가
//        
//        Resource resource = new FileSystemResource(file); // file 객체를 FileSystemResource를 통해 읽을 수 있도록 resource 타입 객체로 생성
//        
//        return ResponseEntity.ok().headers(headers).body(resource);
//    }
}
