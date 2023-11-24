# 🥕 당근나라
중고 거래 사이트</br>
개인 프로젝트
# 사용기술
Java </br>
SQL </br>
Spring Boot </br>
HTML/CSS/Java Script
# 일정
2022.03.20 ~ (진행중)
# 특징
중고 거래를 위한 유저들 간의 채팅 기능 </br>
중고 상품 CRUD (이미지 포함) 및 검색
# 기능
#### 0. 로그인해야 가능한 기능과 아닌 기능 구분
   > SecurityConfig.java 일부
   ```java
   @EnableMethodSecurity(prePostEnabled = true)
   @Configuration
   public class SecurityConfig {
   
       @Bean
       public PasswordEncoder passwordEncoder() {
           return new BCryptPasswordEncoder();
       }
   
       @Bean
       public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
           http.csrf().disable(); // 토큰이 필요한 post/put/delete 요청에 대해 403err를 막고 구현을 간단히 하기 위해
           
           http.formLogin()
               .loginPage("/user/signin")
               .defaultSuccessUrl("/")
               .permitAll();
           
           http.logout()
               .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
               .logoutSuccessUrl("/")
               .invalidateHttpSession(true);
           
           http.authorizeHttpRequests() // 요청에 따른 권한 설정 시작.
               .requestMatchers("") //  로 시작하는 모든 경로 .hasRole("USER")로 원하는 권한에 해당하는 설정
               .hasRole("USER")
               .anyRequest() // 그 이외의 모든 요청
               .permitAll();
           
           return http.build();
       }

       @Bean
       public WebSecurityCustomizer webSecurityCustomizer() {
           return (web) -> {
               web
                   // 정적 자원들(static 폴더 안의 파일들)을 스프링 시큐리티 적용에서 제외시키기 위해서.
                   .ignoring()
                   .requestMatchers(
                       PathRequest.toStaticResources().atCommonLocations()
                   );
           };
       }
       @Bean
       public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
           return authenticationConfiguration.getAuthenticationManager();
       }
   }
   ```
   
   > UserDetailService.java 일부
   ```java
   @Service
   @RequiredArgsConstructor
   public class UserDetailService implements UserDetailsService {
       
       private final UserRepository userRepository;
       
       // 로그인할 때 일치하는 사용자 정보가 있는지 확인하는 과정.
       @Override
       public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
           Optional<User> entity = userRepository.findByUsername(username);
           if(entity.isPresent()) {
               return UserSecurityDto.fromEntity(entity.get());
           } else {
               throw new UsernameNotFoundException(username + "not found!");
           }
       }
   }
   ```

   > User.java 일부
   ```java
    // USER_ROLES 테이블이 생성(컬럼: USER_ID, ROLES)
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default // 초기값이 설정되어 있으면 null이 되지 않음.
    private Set<UserRole> roles = new HashSet<>();  // 권한 부여를 위한 해시셋
    
    public User addRole(UserRole role) { // 유저 권한 부여
        roles.add(role);
        
        return this;
    }
   ```
   > UserRole.java 일부
   ```java
    public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
    
    private String role;

    private UserRole(String role) {
        this.role = role;
    }
    
    public String getRole() {
        return this.role;
    }
   }
   ```

  ##### - 로그인해야 가능한 기능 일부
  > SellController.java 일부
  ```java
    // 판매 상품 등록 DB에 저장
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserSecurityDto userDto, PostCreateDto dto) {
                                     .
                                     .
                                     .
    }
  ```
  > ChatController.java 일부
  ```java
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    @PostMapping("/chat")
    public String chat(@AuthenticationPrincipal UserSecurityDto userDto, Integer postId, Integer sellerId) {
                                     .
                                     .
                                     .
    }
  ```
#### 1. 판매할 중고 상품 등록 및 수정, 삭제(이미지 여러장 등록, 카카오 위치 API)
  ##### 1-1. 중고 상품 등록(Create)
   > SellController.java 일부
   ```java
    // 판매 상품 등록 DB에 저장
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserSecurityDto userDto, PostCreateDto dto) {
        dto.setUserId(userDto.getId());
        log.info("sellCreate(dto={}, {})", dto, dto.getImgIds());
        Integer postId = sellService.create(dto);
        return "redirect:/sell/detail?id=" + postId;
    }
   ```
   > sell.js 일부
   ```java
   window.addEventListener('DOMContentLoaded', () => {
          // 등록하기 버튼 눌렀을 때
          btnSubmit.addEventListener('click', function() {
           if (title == '' || category == '' || prices == '' || content == '') {
               alert('빠진 부분을 채워넣어주세요!');
               return;
           }
   
           const result = confirm('정말 등록하시겠습니까?');
   
           if (result) {
               // 제출했음을 변수에 저장
               submitted = true;
                                     .
                                     .
                                     .
            // axios 작업 기다려 준 후 최종 제출.
            setTimeout(function(){
                // (3) 그 외 나머지 정보 DB에 저장.
                    document.querySelector('#formSell').submit();
                    formSell.action = '/sell/create';
                    formSell.method = 'post';
                    formSell.submit();
            }, 75);
           }
      });
   });
let submitted = false; // 제출 여부 확인

// 제출하지 않고 페이지를 벗어날 시 발생하는 이벤트 리스너(실제 등록되지 않은 로컬저장소 이미지 삭제)
window.addEventListener('beforeunload', function(event) {
       const selectedImage = document.querySelector('#selectedImage');
       if(!submitted && selectedImage.children.length > 0){ // 제출하지 않았을 때, 등록하려던 이미지가 있을 때만 실행
       deleteTemporaryFile();
       }
});
   ```
  ##### 1-2. 중고 상품 등록(이미지 등록)
   > ImageUploadController.java 일부
   ```java
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
   ```
   > img.js 일부
   ```java
    imageInput.addEventListener('change', (event) => {
        const files = event.target.files; // 파일 리스트들에 대한 정보를 files에 담음
        uploadImages(files);
    });

    // 이미지 업로드
    function uploadImages(files) {
        axios.post('/img/upload', files)
            .then(drawInput)
            .catch(err => { alert(err + '인데요, 확인해보세요!!') });
    }
   ```
   > sell.js 일부
   ```java
            // (2) 이미지 파일들 확인
            const selectedImage = document.querySelector('#selectedImage');
            if(selectedImage.children.length > 0){ // 이미지가 있는 경우
                const finalImgs = selectedImage.querySelectorAll('img');
                
                // Array.from()은 유사 배열 객체나 이터러블(iterable) 객체를 배열로 변환하는 메서드
                // Array.from(iterable, mapFn, thisArg) 이런 형태
                // iterable: 배열로 변환할 유사 배열 객체 또는 이터러블 객체
                // mapFn (선택적): 배열의 각 요소에 대해 호출될 맵핑 함수
                // thisArg (선택적): mapFn에서 사용할 this 값을 지정        
                for (let file of finalImgs) {
                    const imgSrc = file.getAttribute('data-src');
                    console.log(file);
                    imageData.push(imgSrc);
                }
                axios.post('/img/upload/db', imageData)
                    .then(response => {
                        const imgIds = response.data.join(', ');
                        const imgs = document.getElementById('imgs');
                        const imgIdsForDbSave = `<div><input class="w3-input w3-border w3-hover-shadow w3-sand" id="imgIds" name="imgIds" value="${imgIds}" readonly/></div>`;
                        imgs.innerHTML += imgIdsForDbSave;
                    })
                    .catch(err => {alert(err+"!!!!");
                    });
                
              }
   ```
  ##### 1-3. 중고 상품 등록(카카오 위치 API)
   > sell.js 일부
   ```java
            // (1) 전달해줄 완성된 전체 주소 input창 만들어주기
            if(detailRegion != '') { // 상세 주소까지 있을 경우
            const region = regionMain + ', ' + detailRegion; // Main주소 + 상세 주소
            const location = document.getElementById('location');
            const jusoStr = `<div><input type="hidden" class="w3-input w3-border w3-hover-shadow w3-sand" id="region" name="region" value="${region}" readonly/></div>`;
            location.innerHTML += jusoStr;
            } else if(regionMain != ''){ // 상세 주소는 없고 Main 주소만 있을 경우
                const location = document.getElementById('location');
                const jusoStr = `<div><input type="hidden" class="w3-input w3-border w3-hover-shadow w3-sand" id="region" name="region" value="${regionMain}" readonly/></div>`;
                location.innerHTML += jusoStr;
            }
   ```
#### 2. 최신 순으로 업데이트된 유저들의 거래 목록

#### 3. 거래를 희망하는 유저들간의 1:1 채팅(Stomp websocket)

#### 4. 웹 서비스 운영을 위한 관리자와 유저들을 위한 편의 서비스

#### 5. 지도 API를 이용한 검색(추정)

#### 6. 유저들 간의 자유 커뮤니케이션(추정)

#### 7. (희망)파이썬을 이용한 인공지능 기능

