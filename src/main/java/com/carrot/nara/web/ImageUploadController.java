package com.carrot.nara.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carrot.nara.dto.FileUploadDto;
import com.carrot.nara.dto.ImageUploadDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.SellService;
import com.carrot.nara.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/img")
public class ImageUploadController {
    
    private final SellService sellService;
    private final UserService userService;
    
    @Value("${com.carrot.nara.upload.path}")
    private String uploadPath; // application.properties 파일에 설정된 값을 읽어서 변수에 할당.
    
    // 허용되는 파일 크기 (바이트 단위)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    // 허용되는 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg",
        "jpeg",
        "png",
        "jfif"
    );
    // 허용되는 MIME 타입 목록
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/jfif"
    );
    
    
    /**
     * 이미지 파일들을 로컬 저장소에 저장
     * @param dto 저장할 이미지 파일들이 list로 전달 받음.(dto는 List<MultipartFile> files를 받도록 되어있음.)
     * @return 올릴 파일들을 미리보기 할 수 있도록 html로 전달.
     */
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
            if(isAllowFile(multipartFile) != null){ // 검증된 파일일 경우만 추가
                FileUploadDto result = saveImg(multipartFile);
                list.add(result);
            }
        });
        
        
        return ResponseEntity.ok(list);
    }
    
    /**
     * UUID 클래스를 통해서 고유의 파일 네임을 만들어서 로컬에 저장하기 위함.
     * @param file MultipartFile type으로 여러 장의 파일을 받아서 UUID_FileName 형태로 만듦
     * @return FileUploadDto type의 UUID, filename, originfilename, image 유무 정보의 result
     */
    // UUID 클래스를 통해서 파일 저장하기 위해 만든 메서드
    // UUID(Universally Unique Identifier)
    // 무작위로 생성되므로 각 실행마다 다른 값이 생성
    // MultipartFile : 웹 애플리케이션에서 파일 업로드를 처리하기 위해 사용되는 객체
    // |__ 이 객체를 통해 파일의 원본 이름, 크기, MIME 타입 등과 같은 정보에 접근 가능. 또한, 파일 데이터 자체에 접근하여 파일을 읽고 저장 가능
    // |__ Spring의 MultipartFile 구현체인 CommonsMultipartFile은 업로드된 파일을 처리하는 데 사용되며, 더 많은 메서드를 제공함.
    // |__ 로컬 파일 시스템이 아니라 메모리나 임시 디렉토리에 파일을 저장하고 처리
    // File : 파일 시스템에서 파일을 나타내는 데 사용, 로컬 파일 시스템에서 파일을 읽거나 쓸 때 사용
    // * 파일 업로드와 관련된 작업에서 File 타입을 직접적으로 사용하기보다는, MultipartFile과 같은 더 편리한 대안을 제공
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
                    .imageFileName(finalFileName)
                    .originFileName(originName)
                    .image(image)
                    .build();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 파일 업로드의 보안을 강화하기 위한 절차(MIME 타입을 확인, 파일의 확장자, 크기 등을 테스트)
     * @param file 검증하려는 file
     * @return 검증된 file, 부적합한 file일 경우 null을 전달
     */
    private MultipartFile isAllowFile(MultipartFile file) {
        log.info("isAllowFile()");
        MultipartFile verifiedFile = null;
        // 업로드된 파일의 MIME 타입 확인
        String mimeType = file.getContentType();
        // 업로드된 파일 이름에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // 업로드된 파일의 크기 확인
        long fileSize = file.getSize();
        
        
        // 허용된 MIME 타입인지 || 확장자를 소문자로 변환하여 허용된 확장자인지 || 파일 크기를 검증
        if (!ALLOWED_MIME_TYPES.contains(mimeType) || 
            !ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) ||
            fileSize > MAX_FILE_SIZE) {
            return verifiedFile;
        } 
        
        verifiedFile = file;
        return verifiedFile;
    }
    
    
    // 로컬에 저장되어 있는 이미지 파일을 보려고 할 때
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> currentImg(@PathVariable String fileName){
        log.info("currentImg({})", fileName);
        return loadResource(fileName);
    }

    // 로컬에 저장되어 있는 유저 이미지 파일을 보려고 할 때
    @GetMapping("/user/{userId}")
    public ResponseEntity<Resource> userImg(@PathVariable Integer userId){
        log.info("userImg({})", userId);
        String fileName = userService.getImageName(userId);
        if(fileName != null) { // 유저 이미지가 있을 때
            return loadResource(fileName);
        } else { // 유저 이미지가 없을 때
            return loadResource("noProfile.png");
        }
    }
    
    /**
     * 파일 이름을 통해 파일 불러오기
     * @param fileName 파일이름
     * @return 읽어온 파일을 반환 headers(HTTP 응답 헤더(Content-Type 헤더 필드)를 설정), body(resource 타입 객체)로 구성
     */
    private ResponseEntity<Resource> loadResource(String fileName){
        
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
    
    // 로컬에 저장되어 있는 선택된 이미지 파일 삭제
    @DeleteMapping("/upload/{fileName}")
    public ResponseEntity<?> currentDeleteImg(@PathVariable String fileName){
        log.info("currentDeleteImg({})", fileName);
        File file = new File(uploadPath, fileName);
        boolean result = file.delete();
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패 상태 코드 500 반환
        }
    }
    
    /***
     * 등록할 이미지들을 SQL DB에 저장
     * @param imageData 업로드할 file_name으로 구성된 배열
     * @return 이미지 ID들 전달. 최종적으로 POST 글 생성하기 전에 postId를 postImage DB에도 저장하기 위함.
     */
    @PostMapping("/upload/db")
    public ResponseEntity<List<Integer>> saveImgForDb(@RequestBody String[] imageData){
        log.info("saveImgForDb()");
        for (String f : imageData) {
            log.info("imageData={}", f);
        }
        List<Integer> imgIds = sellService.createImg(imageData);
        return ResponseEntity.ok(imgIds);
    }
    
    /**
     * 등록하려다가 안하는 경우에 로컬에 저장된 이미지를 삭제.
     * @param temporaryData 최종 등록이 되지 않은 이미지 파일들의 이름들의 집합.
     * @return 성공 문자열
     */
    @DeleteMapping("/delete/{temporaryData}")
    public ResponseEntity<String> deleteTemporaryFile(@PathVariable String[] temporaryData) {
        log.info("deleteTemporaryFile()");
        for (String f : temporaryData) {
            File file = new File(uploadPath, f);
            file.delete();
        }
        
        return ResponseEntity.ok("success");
    }
    
    /**
     * 유저 이미지를 변경하기
     * @param user 변경하려는 유저의 정보
     * @param file 변경하게 될 유저의 이미지(선택한 이미지 파일이 FormData에 추가되어 서버로 전송되며, 서버 컨트롤러에서 해당 파일을 MultipartFile로 받아 처리)
     * @return 성공하면 ok(), 실패하면 badRequest()를 반환
     */
    @PostMapping("/upload/profile")
    public ResponseEntity<?> saveProfile(@AuthenticationPrincipal UserSecurityDto user, MultipartFile file){
        log.info("saveProfile({},{})", user.getNickName(), file.getOriginalFilename());
        if(isAllowFile(file) != null) {
            FileUploadDto uploadFile = saveImg(file);
            userService.saveUserProfileImage(user.getId(), uploadFile.getImageFileName());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
