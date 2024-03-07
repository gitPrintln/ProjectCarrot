# 🥕 당근나라
중고 거래 사이트</br>
개인 프로젝트
# 사용기술
Java </br>
SQL, Redis </br>
Spring Boot </br>
HTML/CSS/Java Script
# 일정
2023.03.20 ~ 2023.10.30
# 특징
중고 상품 CRUD (이미지 포함) 및 검색 </br>
중고 거래를 위한 유저들 간의 채팅 기능 </br>
유저 활성화를 위한 서비스
# 기능 소개
#### 0. 로그인해야 가능한 기능과 아닌 기능 구분
#### 1. 판매할 중고 상품 등록 및 수정, 삭제(이미지 여러장 등록, 카카오 위치 API)
#### 2. 최신 순으로 업데이트된 유저들의 거래 목록(검색)
#### 3. 중고 상품 글 자세히 보기
#### 4. 거래를 희망하는 유저들간의 1:1 채팅(Stomp websocket, Redis)
#### 5. 웹 서비스 운영을 위한 관리자와 유저들을 위한 편의 서비스
#### 6. 지도 API를 이용한 검색(추정)
#### 7. 유저들 간의 자유 커뮤니케이션(추정)
#### 8. (희망)파이썬을 이용한 인공지능 기능
# 기능 구현
#### 0. 로그인해야 가능한 기능과 아닌 기능 구분
  - SpringSecurity 설정
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
  ##### 1-1. 중고 상품 글
  - 등록 및 수정
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
            }, 100);
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
  - 삭제
   > modify.js 일부
   ```java
    // 삭제 버튼 이벤트
   window.addEventListener('DOMContentLoaded', () => {
    const btnDelete = document.getElementById('btnDelete');
    btnDelete.addEventListener('click', function(){
        const result = confirm('글을 삭제하게 되면 현재 판매와 관련된 채팅방은 삭제됩니다. 정말 삭제하시겠습니까?');
        if(result){
            alert('글을 삭제했습니다.')
            document.querySelector('#formModify').submit();
            formModify.action = '/sell/delete';
            formModify.method = 'post';
            formModify.submit();
        }
    });
});
// 이미지를 추가하려고 시도했지만 최종 등록 되지 않은 이미지 삭제(이미 있는 이미지는 건드리지 않음)
function deleteTemporaryFile(){
    let temporaryData = [];
    const selectedImage = document.querySelector('#selectedImage');
    const addedImgs = selectedImage.querySelectorAll('img');
        addedImgs.forEach(file => {
            const imgSrc = file.getAttribute('data-src');
            if (!initialImageData.includes(imgSrc)) { // 초기 이미지 데이터에 저장되어 있지않은 추가하려고 시도했던 이미지만 삭제행
                temporaryData.push(imgSrc);
            }
        });
        axios.delete('/img/delete/' + temporaryData)
            .then(response => {console.log('이미지 삭제 완료');
            })
            .catch(err=>{console.log(err)});
}
   ```
   > SellController.java 일부
   ```java
    // 포스트 글과 그 글의 이미지, 로컬저장소의 이미지, 관련 채팅방 모두 삭제
    @Transactional
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserSecurityDto userDto, Integer id) {
        log.info("sellUpdate(postID={})", id);
        sellService.deletePost(id);
        return "redirect:/list";
    }
   ```
  ##### 1-2. 중고 상품 글 이미지
   - 이미지 업로드 편의 기능
   * 로컬 저장소에 저장하는 시점과 DB에 이미지 파일을 불러오는 시점을 분리해뒀기 때문에 사용자 편의를 위해 업로드 후 드래그로 여러 장 업로드, 추가 업로드, 추가 삭제가 가능함.
   ![bandicam 2024-01-01 00-17-46-453](https://github.com/gitPrintln/ProjectCarrot/assets/117698468/007dc382-df23-434e-b0de-589e84390907)
   
   - 등록
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
   - 수정 및 삭제
   > modify.js 일부
   ```java
let initialImageData = []; // 초기 이미지 정보를 저장해두고 최종 수정하지 않거나 이탈할 경우 이용(전역 변수로 사용)

window.addEventListener('DOMContentLoaded', () => {
        // 불러온 이미지가 있으면, 최종 수정까지 하게 될 경우를 대비해서 이미지 정보 저장
        const initialImgs = selectedImage.querySelectorAll('img');
        for (let file of initialImgs) {
              const iniImgSrc = file.getAttribute('data-src');
              initialImageData.push(iniImgSrc);
        }
        // * 최종이미지 DB에 저장할 dataForm(initialImgs를 제외한 추가된 이미지)
        let imageData = []; //  HTML <form> 요소의 데이터를 캡슐화하고, Ajax를 통해 서버로 전송하기 위해 사용
    
            // (2) 이미지 파일들 확인
            const selectedImage = document.querySelector('#selectedImage');
            if(selectedImage.children.length > 0){ // 이미지가 있는 경우
                const finalImgs = selectedImage.querySelectorAll('img');
                
                // 로직 : 최종 저장하려는 이미지를 초기 저장되어있는 이미지와 비교를 하여
                // 초기 이미지에 포함되지 않은 이미지면 최종 저장 이미지에 올려서 저장을 하고
                // 초기 이미지에 포함된 이미지라면 초기 이미지에서 해당 이미지를 빼주고 남은 이미지를 추려냄
                // 반복하면 결과는,
                // 최종 저장하려는 이미지는 추가 저장과 초기 이미지는 삭제해야할 이미지만 남게 된다.
                for (let file of finalImgs) {
                    const imgSrc = file.getAttribute('data-src');
                    console.log(file);
                    if(!initialImageData.includes(imgSrc)){ // ㄱ-초기 이미지 데이터에 저장되어 있지 않은 추가하려고 하는 추가 이미지만 DB에 저장
                        imageData.push(imgSrc);
                    } else{ // ㄴ-초기 이미지 중 삭제된 이미지 가려내서 로컬저장소와 DB에 삭제
                        const indexToRemove = initialImageData.indexOf(imgSrc); // 해당 요소의 인덱스 값을 찾은 후
                        initialImageData.splice(indexToRemove, 1); // 인덱스부터 시작해서 1개의 값을 제거
                    }
                }

                // Promise 체이닝은 비동기 작업을 순차적으로 연결하여 처리하는 방식
                // 비동기 작업을 처리하기 위해 Promise를 사용하며, 이러한 Promise 객체들을 연속적으로 연결하여 작업을 수행하는 것을 Promise 체이닝
                axios.post('/img/upload/db', imageData)
                    .then(response => {
                        const imgIds = response.data.join(', ');
                        const imgs = document.getElementById('imgs');
                        const imgIdsForDbSave = `<div><input class="w3-input w3-border w3-hover-shadow w3-sand" id="imgIds" name="imgIds" value="${imgIds}" readonly/></div>`;
                        imgs.innerHTML += imgIdsForDbSave;
                        
                        if(initialImageData.length > 0) { // 삭제되어야할 데이터가 있으면 실행
                            axios.delete('/img/delete/db/' + initialImageData)
                                .then(response => {console.log('이미지 삭제 완료');
                                })
                                .catch(err=>{console.log(err)});
                        }
                    })
                    .catch(err => {alert(err+"!!!!");
                    });
});
   ```
   > ImageUploadController.java 일부
   ```java
    /**
     * 저장되어 있는 이미지를 삭제하는 경우(로컬저장소와 DB에서 삭제)
     * @param initialImageData 유저가 포스트글 업데이트를 통해 로컬저장소와 DB에 삭제하려는 이미지 파일들의 이름들의 집합.
     * @return 성공 문자열
     */
    @DeleteMapping("/delete/db/{initialImageData}")
    public ResponseEntity<String> deleteUpdatedImageFile(@PathVariable String[] initialImageData) {
        log.info("deleteUpdatedImageFile()");
        for (String f : initialImageData) {
            File file = new File(uploadPath, f);
            file.delete(); // 로컬에서 파일 삭제
            sellService.deleteImg(f); // DB에서 파일 삭제
        }
        return ResponseEntity.ok("success");
    }
   ```
   > sell.html 일부
   ```java
        <div><input type="file" style="display: none;" class="w3-input w3-border w3-sand" id="images" name="images" multiple accept=".jpg, .jpeg, .png, .jfif"/></div>
        </div>
        <!-- 선택한 파일이 보여지는 공간 -->
        <div id="selectedImage"></div>
   ```
  ##### 1-3. 중고 상품 글 주소(카카오 위치 API)
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
   * (gif를 넣을 곳)
#### 2. 최신 순으로 업데이트된 유저들의 거래 목록(검색)
  - 거래 목록 불러오기(상단에서 검색 포함)
  ![bandicam 2024-01-01 00-33-15-686](https://github.com/gitPrintln/ProjectCarrot/assets/117698468/d67c39be-28a1-42c0-a2f2-5a69fb6859b8)
   > ListController.java 일부
   ```java
    /**
     * 해당 포스트 글 + 포스트 이미지로 구성된 ListReadDto 타입의 list를 불러옴.
     * @param keyword 검색 키워드가 있을 경우의 keyword, 전체 리스트인 경우 null로 전달
     * @return ListReadDto 타입의 List를 전달해줌.
     */
    private List<ListReadDto> loadList(String keyword){
        log.info("loadList()");
        List<ListReadDto> list = new ArrayList<>(); // 최종 리스트
        List<Post> postList = new ArrayList<>(); // 포스트에 대한 리스트(이미지 x) - 최종리스트에 넣기 위해 작업해줘야함. 
        
        if(!keyword.equals("")) { // 검색 키워드로 리스트 불러올 때(상단의 검색창으로 검색시)
            postList = postService.readByKeywordByUpdateTime(keyword);
        } else { // 전체 리스트 불러올 때(상단의 중고거래 목록 보기 클릭시)
            postList = postService.readAllByUpdateTime();
        }
        
        for (Post p : postList) {
            // 이미지가 있을 수도 있고 없을 수도 있기 때문에 optional로 조회 후 optional로 감싸서 객체를 생성
            // 주의, 전달된 값이 null이라면 Optional.empty()를 반환함.
            Optional<PostImage> pi = Optional.ofNullable(postService.readThumbnail(p.getId()));
            String imageFileName = "image-fill.png"; // 포스트 글의 이미지가 없으면 기본 이미지를 넣음.
            String lastModifiedTime = TimeFormatting.formatting(p.getModifiedTime());
            
            if(pi.isPresent()) { // 포스트 글의 이미지가 있으면 있는 이미지로 교체
                PostImage pig = pi.get();
                imageFileName = pig.getFileName();
            }
            ListReadDto listElement = ListReadDto.builder().id(p.getId()).imageFileName(imageFileName)
                    .title(p.getTitle()).region(p.getRegion())
                    .prices(p.getPrices()).chats(p.getChats()).hits(p.getHits()).wishCount(p.getWishCount())
                    .status(p.getStatus())
                    .modifiedTime(lastModifiedTime).build();
            list.add(listElement);
        }
        
        return list;
    }
   ```
   > list.html 일부
   ```java
        <div class="listPostCard w3-container" th:each="list : ${ list }"><!-- 카드 하나 -->
          <div class="title">
            <span th:text="${ list.title }"></span>
          </div>
          <div class="region">
            <span th:if="${ list.region != null }" style="font-size: 10px;" th:text="${ list.region.split(' ')[1] + ' ' + list.region.split(' ')[2] }"></span>
          </div>
          <div class="prices">
             <span th:text="${ list.prices }"></span><span>&nbsp;원</span><br/><br/>
          </div>                      
        </div><!-- 카드 하나 end -->
   ```

#### 3. 중고 상품 글 자세히 보기
  ##### 3-1. 이미지 슬라이드 기능
   > detail.html 일부
   ```html
                <!-- 이미지 시작 -->
                     <div class="w3-content w3-display-container imgsSlide">
                        <div class="mySlides" th:each="i : ${ postImage }">
                            <img th:src="${ '/img/view/'+ i.fileName }">
                        </div>
                        <!-- next, prev 버튼, indicator 버튼들 -->
                        <div class="slideBtnsDiv">
                        </div>
                     </div>
                <!-- 이미지 끝 -->
   ```
   > detail.js 일부
   ```js
    // slide imgs 관련
    var slideIndex = 1;
    var mySlidesItems = document.getElementsByClassName('mySlides');
    indicatorsController();
    // carousel-indicators 컨트롤러
    function indicatorsController(){
        const indicatorsCount = mySlidesItems.length;
        if(indicatorsCount>1) { // 이미지 개수가 1개보다 많을 때만 가운데 버튼과 양쪽 prev, next button 보이게 하기.
            // next, prev 버튼
            const slideBtnsDiv = document.querySelector('.slideBtnsDiv');
            let nextPrevBtnStr = '';
            nextPrevBtnStr += '<div class="w3-center w3-container w3-section w3-large w3-text-white w3-display-bottommiddle" style="width:100%">'
                + '<div class="w3-left w3-hover-text-khaki" onclick="plusDivs(-1)">&#10094;</div>'
                + '<div class="w3-right w3-hover-text-khaki" onclick="plusDivs(1)">&#10095;</div>'
                + '<!-- 아래쪽 버튼 개수 동적으로 변경 -->'
                + '<div class="indicatorsBtnDiv">'
                + '</div>'
                + '</div>';
                slideBtnsDiv.innerHTML += nextPrevBtnStr;
            // indicators 버튼
            const indicatorsBtnDiv = document.querySelector('.indicatorsBtnDiv');
            for(let i=0; i < indicatorsCount; i++){
                   const slideBtnStr = `<span class="w3-badge demo w3-border w3-transparent w3-hover-white" onclick="currentDiv(${i+1})"></span>`
                       indicatorsBtnDiv.innerHTML += slideBtnStr;
            }
            
            // slides script
            showDivs(slideIndex);
            
        } else if(indicatorsCount === 1){ // 이미지 개수가 딱 1개일 때
            var y = document.getElementsByClassName("mySlides");
            y[slideIndex-1].style.display = "block";
        }
    }

    function plusDivs(n) {
      showDivs(slideIndex += n);
    }

    function currentDiv(n) {
      showDivs(slideIndex = n);
    }

    function showDivs(n) {
      var i;
      var x = document.getElementsByClassName("mySlides");
      var dots = document.getElementsByClassName("demo");
      if (n > x.length) {
          slideIndex = 1
      }
      if (n < 1) {
          slideIndex = x.length
      }
      for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";  
      }
      for (i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" w3-white", "");
      }
      x[slideIndex-1].style.display = "block";  
      dots[slideIndex-1].className += " w3-white";
    }
   ```
  ##### 3-2. 판매중/판매완료/예약중 기능(작성자가 본인인 경우)
   > detail.html 일부
   ```html
                        <div id="divStatus" class="w3-right status">
                        <!-- 비회원은 그냥 보여줌 -->
                        <div th:if="${ #authentication.name == 'anonymousUser'}">
                            <span id="spanStatus" th:text ="${ p.status }" ></span>
                        </div>
                        <!-- 회원일 때 글 작성자와 동일한지 아닌지에 따라 수정가능 여부 -->
                        <div th:if="${ #authentication.name != 'anonymousUser'}">
                            <div class="statusDiv w3-dropdown">
                            <span id="spanStatus" th:onclick="${ p.userId == #authentication.principal.id } ? 'statusModifing()' : ''"
                                  th:style="${ p.userId == #authentication.principal.id } ? 'cursor: pointer;' : ''" 
                                  th:text ="${ p.status }" ></span>
                            <div id="statusChangeOption" class="w3-dropdown-content w3-bar-block w3-card-4">
                            <a href="javascript:void(0)" class="w3-bar-item w3-button" th:data-pid="${p.id}" onclick="statusChanging(event)">판매중</a>
                            <a href="javascript:void(0)" class="w3-bar-item w3-button" th:data-pid="${p.id}" onclick="statusChanging(event)">예약중</a>
                            <a href="javascript:void(0)" class="w3-bar-item w3-button" th:data-pid="${p.id}" onclick="statusChanging(event)">판매완료</a>
                            </div>
                            </div>
                        </div>
                        </div>
   ```
   > detail.js 일부
   ```js
window.addEventListener('DOMContentLoaded', () => {
    statusFontColor(); // 글의 판매 상태에 따라 폰트색상 변경
});
    // 글 작성자와 로그인 유저가 동일하다면 버튼 클릭으로 status 수정 가능하도록 해주기
    // 클릭했을 경우 변경 가능한 드롭다운 보여주기
    function statusModifing(){
      var optionBox = document.getElementById("statusChangeOption");
      if (optionBox.className.indexOf("w3-show") == -1) { 
        optionBox.className += " w3-show";
      } else {
        optionBox.className = optionBox.className.replace(" w3-show", "");
      }
    }
    
    // 판매중, 예약중, 판매완료 눌렀을 때 axios로 즉시 바꿔줌.
    function statusChanging(event){
        var postId = event.target.getAttribute('data-pid');
        const toChangeSts = event.target.text;
        const data = {
            id: postId,
            status: toChangeSts
        };
        axios.post('/sell/modify/status', data)
            .then(response => {
                const spanStatus = document.getElementById('spanStatus');
                if(toChangeSts === "예약중"){
                    spanStatus.style.color = 'green';
                } else if(toChangeSts === "판매완료"){
                    spanStatus.style.color = 'red';
                } else{
                    spanStatus.style.color = 'blue';
                }
                spanStatus.innerHTML = toChangeSts;
                statusModifing();
                alert(toChangeSts + ' 상태로 변경하였습니다.');
                
            })
            .catch(err => {console.log(err)});
    }    
    
    // 판매중, 예약중, 판매완료 각각의 조건에 따라 폰트색깔 동적으로 변경
    function statusFontColor(){
        const statusFontColor = document.getElementById('spanStatus');
        const spanStatus = statusFontColor.innerHTML;
        if(spanStatus === "예약중"){
            statusFontColor.style.color = 'green';
        } else if(spanStatus === "판매완료"){
            statusFontColor.style.color = 'red';
        }
    }
   ```
    -> 판매 상태에 따라서 거래목록에 다른 효과
  ##### 3-3. 좋아요 기능
   > detail.html 일부
   ```html
                        <div id="btnHeart" th:data-postId="${p.id}" style="display: inline-block; cursor:pointer;">
                            <img id="emptyHeart" th:style="${ like != '좋아요' } ? 'width: 30px;' : 'display: none; width: 30px;'" src="/images/empty-heart.png" alt="좋아요 누르기">
                            <img id="fullHeart" th:style="${ like == '좋아요' } ? 'width: 30px;' : 'display: none; width: 30px;'" src="/images/full-heart.png" alt="좋아요 취소하기">
                        </div>
   ```
   > detail.js 일부
   ```js
window.addEventListener('DOMContentLoaded', () => {
    // 좋아요 -> 취소, 취소 -> 좋아요
    const btnHeart = document.querySelector('#btnHeart');
    btnHeart.addEventListener('click', function(){
        const postId = btnHeart.getAttribute('data-postId');
        const emptyHeart = document.querySelector('#emptyHeart');
        const fullHeart = document.querySelector('#fullHeart');
        const wishNum = document.querySelector('#wishNum');
        axios.get('/myPage/postLike', {
                params: {
                    postId: postId
                }
            })
            .then(likeStatus => {
                if(likeStatus.data === '좋아요'){
                    fullHeart.style.display = '';
                    emptyHeart.style.display = 'none';
                    alert('좋아요!!');
                    
                    // 전체 관심수 개수 반영해주기
                    axios.get('/sell/wishCount', {
                        params: {
                            postId: postId
                        }
                    }).then(wishCounts =>{
                        wishNum.textContent = '관심 ' + wishCounts.data;
                    }).catch(err => console.log(err + '전체 좋아요 개수 문제'));
                }else if(likeStatus.data === '좋아요 취소'){
                    fullHeart.style.display = 'none';
                    emptyHeart.style.display = '';
                    alert('좋아요 취소!!');
                    
                    // 전체 관심수 개수 반영해주기
                    axios.get('/sell/wishCount', {
                        params: {
                            postId: postId
                        }
                    }).then(wishCounts =>{
                        wishNum.textContent = '관심 ' + wishCounts.data;
                    }).catch(err => console.log(err + '전체 좋아요 개수 문제'));
                }else{
                    alert('로그인 후 이용 부탁드립니다.');
                }
            })
            .catch(err => console.log(err+'좋아요 에러 확인!!'));
    });
});
   ```
   > MyPageController.java 일부
   ```java
public class MyPageController {
    /**
     * user가 누른 좋아요를 DB에 반영, 해당 post글의 관심수 1올려줌
     * @param postId post글
     * @return
     */
    @PreAuthorize("hasRole('USER')")
    @Transactional
    @GetMapping("/postLike")
    @ResponseBody
    public ResponseEntity<String> postLike(@AuthenticationPrincipal UserSecurityDto user, Integer postId){
        log.info("postLike(userId={}, postId={})", user.getId(), postId);
        String likeStatus = myPageService.likeStatusChg(user.getId(), postId);
        return ResponseEntity.ok(likeStatus);
    }
}
   ```
   > MyPageService.java 일부
   ```java
public class MyPageService {
    /**
     * DB에 좋아요 상태를 변경하고 해당 글의 관심수를 변경해줌.
     * @param id userId
     * @param postId postId
     * @return 변경 완료 후 좋아요 상태
     */
    @Transactional
    public String likeStatusChg(Integer id, Integer postId) {
        log.info("likeStatusChg()");
        String likeStatus = "좋아요";
        
        // 좋아요 누른 것이 DB에 조회된다면? DB에 삭제 해주고, 상태를 좋아요 취소로 바꿔줌.
        Optional<PostLike> postLikeByUser = Optional.ofNullable(postLikeRepository.findByUserIdAndPostId(id, postId));
        if(postLikeByUser.isPresent()) {
            postLikeRepository.deleteById(postLikeByUser.get().getId());
            postRepository.uplikesCancel(postId);
            return likeStatus = "좋아요 취소";
        }
        
        // 좋아요되어 있지 않은 상태라면? DB에 추가 해주고 좋아요 상태로 전달해줌.
        PostLike entity = PostLike.builder().userId(id).postId(postId).build();
        postLikeRepository.save(entity);
        postRepository.uplikes(postId);
        return likeStatus;
    }
}
   ```
  ##### 3-4. 수정/채팅 기능(작성자가 본인인 경우/아닌 경우)
   > detail.html 일부
   ```html
                        <div style="margin: 10px; display: inline-block;">
                            <!-- 비로그인(anonymousUser)일 때 '채팅하기' -->
                            <button type="button" th:if="${ #authentication.name == 'anonymousUser' }" onclick="anonymousLogin();" class="w3-button w3-pale-red w3-round-xlarge">채팅하기</button>
                            <!-- 글작성자와 로그인 유저가 다를 때는 '채팅하기', 같을 때는 '수정하기' -->
                            <div th:if="${ #authentication.name != 'anonymousUser'}">
                                <button type="button" th:if="${ p.userId != #authentication.principal.id }" th:data-pid="${p.id}" th:data-sid="${p.userId}" th:onclick="connectChat(event);" class="w3-button w3-pale-red w3-round-xlarge">채팅하기</button> 
                                <button type="button" th:if="${ p.userId == #authentication.principal.id }" th:data-pid="${p.id}" th:onclick="|location.href='@{ /sell/modify?postId='+ this.getAttribute('data-pid') + '}';|" class="modifiedBtn w3-button w3-pale-red w3-round-xlarge">수정하기</button> 
                            </div>
                        </div>
   ```
#### 4. 거래를 희망하는 유저들간의 1:1 채팅(Stomp websocket, Redis)
  - 채팅 설정
   > WebSocketMessageBroker.java 일부
   ```java
      @EnableWebSocketMessageBroker
      @Configuration
      public class WebSocketMessageBroker implements WebSocketMessageBrokerConfigurer {
          
          @Override
          public void configureMessageBroker(MessageBrokerRegistry registry) {
              // 메시지를 구독하는 요청 url prefix => 즉 메시지 받을 때 (subscribe, sub)
              registry.enableSimpleBroker("/user");
      
              // 메시지를 발행하는 요청 url prefix => 즉 메시지 보낼 때 (publish, pub)
              registry.setApplicationDestinationPrefixes("/app");
          }
          
          @Override
          public void registerStompEndpoints(StompEndpointRegistry registry) {
              registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
          }
      }
   ```
  ##### 4-1. 채팅 연결
   > chat.js 일부
   ```java
    // SockJS 연결
    function connect() {
            // map URL using SockJS 
            var socket = new SockJS('/chat');
            var url = '/user/queue/messages/' + chatId;
            var notificationUrl = '/user/notification/' + chatId + "/" + chatPartnerId;
            // webSocket 대신 SockJS을 사용하므로 Stomp.client()가 아닌 Stomp.over()를 사용함
            stompClient = Stomp.over(socket);
            // connect(header, connectCallback(==연결에 성공하면 실행되는 메서드))
            stompClient.connect({}, function() { 
                autofocus();
                alarm(); // redis에 채팅방에 접속했음을 저장하고 상대에게는 채팅방에 들어왔음을 알림(안읽은 메세지를 읽음으로)
                
                // url: 채팅방 참여자들에게 공유되는 경로(message용)
                // callback(function()): 클라이언트가 서버(Controller broker)로부터 메시지를 수신했을 때(== STOMP send()가 실행되었을 때) 실행
                stompClient.subscribe(url, function(output) { // 메세지에 관한 구독
                    // html <body>에 append할 메시지 contents
                    showBroadcastMessage(createTextNode(JSON.parse(output.body)));
                    autofocus();
                    updateList();
                });
                
                // notificationUrl: 채팅방 참여자들에게 공유되는 경로(알림용) 읽음/안읽음 등
                stompClient.subscribe(notificationUrl, function(output){
                    responseAlarmCheck(output.body); // 어떤 종류의 알람인지 확인 후 그에 맞는 처리
                });
                }, 
                    // connect() 에러 발생 시 실행
                        function (err) {
                            console.log('error' + err);
            });
 
        };
   ```
   > ChatController.java 일부
   ```java
    public String chat(@AuthenticationPrincipal UserSecurityDto userDto, Integer postId, Integer sellerId) {
        Optional<Chat> isPresentLoadedChat = Optional.ofNullable(chatService.loadChat(userId, postId, sellerId));
        if(isPresentLoadedChat.isPresent()) { // 불러온 채팅이 이미 존재하는 경우
            log.info("post글의 detail에서 채팅창으로 연결하는 경우 대화내역 이미 존재하는 경우");
            Chat loadedChat = isPresentLoadedChat.get();
            chatId = loadedChat.getId();
            return "/chat?chatId="+chatId;
        }
        // 새로운 채팅을 생성해주는 경우
        log.info("post글의 detail에서 채팅창으로 연결하는 경우 대화내역 없어서 생성해줄 경우");
        Chat newChatInfo = chatService.createNewChat(userId, postId, sellerId);
        return "/chat?chatId="+chatId;
    }

    /**
     * 채팅 목록을 불러옴
     * @param userId 불러오고자 하는 user의 id
     * @return ChatListDto 타입의 리스트
     */
    @Transactional(readOnly = true)
    public List<ChatListDto> loadChatList(Integer userId){
        log.info("loadChatList(userId={})", userId);
        
        // 내가 속해 있는 모든 대화 채팅 목록(어디서 채팅방으로 들어가던지 공통)
        List<Chat> chatList = chatService.myChatList(userId);
        
        List<ChatListDto> list = new ArrayList<>(); // chat 목록에 사용될 최종 list
        
        // lastTime 값이 있는지 없는지 비교해보기위한 기준 time
        LocalDateTime benchmarkTime = LocalDateTime.of(1111, 11, 11, 11, 11);
        for (Chat chat : chatList) {
            String sellerNick = userService.getNickName(chat.getSellerId());
            String partnerNick = userService.getNickName(chat.getUserId());
            String lastChat = redisService.getLastChat(chat.getId());
            LocalDateTime lastTimeBeforeFormat = redisService.getLastTime(chat.getId());
            String lastTime = "";
            if(!lastTimeBeforeFormat.isEqual(benchmarkTime)) { // 값이 없다면 formatting하면 안되기 때문에
                lastTime = TimeFormatting.formatting(lastTimeBeforeFormat);
            } 
            ChatListDto entity = ChatListDto.builder()
                                .id(chat.getId()).partnerId(chat.getUserId())
                                .sellerId(chat.getSellerId()).sellerNickName(sellerNick).partnerNickName(partnerNick)
                                .lastChat(lastChat).lastTime(lastTime)
                                .build();
            list.add(entity);
        }
        return list;
    }
   ```
  - 채팅 연결
  * (gif를 넣을 곳)
  ##### 4-2. 읽음/안읽음 기능
   > Message.java 일부
   ```java
    public class Message extends TimeEntity {
        @Builder.Default
        private Integer readChk = 1; // 읽었으면 0, 안읽었으면 1(default = 1)
    }
   ```
   > chat.js 일부
   ```java
    // SockJS 연결
    function connect() {
            stompClient.connect({}, function() {
              // notificationUrl: 채팅방 참여자들에게 공유되는 경로(알림용) 읽음/안읽음 등
              stompClient.subscribe(notificationUrl, function(output){
              responseAlarmCheck(output.body); // 어떤 종류의 알람인지 확인 후 그에 맞는 처리
              });
            },

    if(chatPartner != null){ // 채팅 상대방이 있는 경우에만 활성화함.
    // 채팅 입력창에 포커싱을 맞출때 상대방에게 읽음 알림 보내기
    messageInput.addEventListener('focus', alarm);
    }
    // Redis에 채팅방에 접속중인 유저로 저장하고 채팅방에 들어왔음을 알릴 때
    // 채팅창에 포커싱 맞춰졌을 때 읽음으로 간주하고 상대에게 읽음을 알릴 때
    function alarm(){
        const json = {'userNick': sender, 'userId': senderId, 'partnerId': chatPartnerId};
        stompClient.send("/app/chatNotification/" + chatId, {}, JSON.stringify(json));
    }

    // 어떤 종류의 알람인지(읽음/안읽음을 위한 처리, 로그인 알람, 채팅방 리스트 변경)
    function responseAlarmCheck(str){
        if(str === "ChatPartner's Notification"){
               chatPartnerAlarm();
        } 
    };

    // 읽음/안읽음을 위한 처리(읽었으면 1->0으로 지워줌)
    function chatPartnerAlarm(){
       console.log("상대방로그인입니다.");
       const read = document.querySelectorAll('#reads');
       for (var i = 0; i < read.length; ++i) {
             read[i].style.visibility = 'hidden';
             read[i].removeAttribute('id');
       }
    }
    }
   ```
   > ChatController.java 일부
   ```java
    /**
     * 채팅방 알림용(로그인, 읽음 처리, 리스트 갱신)
     * ChatAlarmDto의 alarmNo가 0: 채팅방 읽음 알람, 1: 채팅방 리스트 갱신 알람
     * @param chatId
     * @param dto
     * @throws IOException
     */
    @MessageMapping("/chatNotification/{chatId}")
    public void chatAlarm(@DestinationVariable Integer chatId, ChatAlarmDto dto) throws IOException{
        log.info("chatAlarm(chatId={}, alarmNo={}, loginUser={}, loginUserId={})", chatId, dto.getAlarmNo(), dto.getUserNick(), dto.getUserId());
        boolean checkLoginUser = redisService.isLogInChatRoom(dto.getUserId());
        
        if(dto.getAlarmNo() == 0) { // 상대방이 채팅방 읽음 알람일 경우
        // 처음 채팅방에 들어왔을 경우
        if(!checkLoginUser) {
            // 이제 로그인하는 경우 redis에 저장
            redisService.registerLogInChatRoom(dto.getUserId());
        } 
        
        // 서로 로그인 상태에서 대화중일 경우(따로 해줄 것은 없음)
       
        // DB에 로그인한 유저의 안읽은 메세지를 읽음으로 바꿔줌
        String partnerNick = userService.getNickName(dto.getPartnerId());
        chatService.unreadToRead(chatId, partnerNick);
        
        // url을 채팅 상대방에게 설정해서 convertAndSend해야지 상대방 화면에서 안읽음 메세지를 읽음으로바꾸지
        String url = "/user/notification/" + chatId + "/" + dto.getUserId();
        simpMessagingTemplate.convertAndSend(url, "ChatPartner's Notification");
        } 
    }
   ```
   * (gif를 넣을 곳)
  ##### 4-3. 리스트에 마지막 채팅 보여주기(redis에 저장된 캐시로 빠르게 불러옴)
   > RedisService.java 일부
   ```java
    public class RedisService {
        // redis에 lastChat 캐시를 설정해둠.
        private void setCacheLastChat(Integer chatId, String lastChat) {
            log.info("cacheLastChat()");
            String id = "lastChat. chatId: " + chatId;
            redisTemplate.opsForValue().set(id, lastChat);
        }
    
        // redis cache 중 lastChat을 가져옴.
        private String getLastChatFromCache(Integer chatId) {
            log.info("getLastChatFromCache()");
            String id = "lastChat. chatId: " + chatId;
            return redisTemplate.opsForValue().get(id);
        }
        
        /**
         * 1. redis lastChat 캐시 데이터를 확인 후 없으면 2. jpaRepository에서 찾아옴 + lastChat 캐시에 저장.
         * @param chatId 마지막 챗을 가져올 chatId
         * @return 마지막 챗
         */
        @Transactional(readOnly = true)
        public String getLastChat(Integer chatId) {
            log.info("getLastChat(id={})", chatId);
            String lastChat = getLastChatFromCache(chatId);
            if(lastChat == null) { // 캐시 데이터가 없으면 oracle sql에서 조회후 찾아와서 캐시에 저장.
                // 방만 만들어져있고 채팅 내용이 없을 경우가 있을 수 있으므로
                Optional<Message> message = Optional.ofNullable(messageRespository.findFirstByChatIdOrderByModifiedTimeDesc(chatId));
                if(message.isPresent()) {
                    lastChat = message.get().getMessage();
                    setCacheLastChat(chatId, lastChat);
                } else {
                    lastChat = "";
                }
            }
            return lastChat;
        }
        // lastChat이 바뀌었을 경우. redis lastChat 캐시도 수정해줌.(+ 시간도 추가)
        // 메세지 내용과 시간으로 같이 한꺼번에 저장하면 관리할 키 개수는 줄어드는 장점이 있지만
        // 유지보수 관리 측면에서 내용과 시간을 나누는 것이 더 나을 것 같다는 생각으로 나눠서 저장.
        public void modifiedLastChat(Integer chatId, String message, LocalDateTime sendTime) {
            log.info("modifiedLastChat()");
            String messageId = "lastChat. chatId: " + chatId;
            String timeId = "lastTime. chatId: " + chatId;
            redisTemplate.opsForValue().set(messageId, message);
            redisTemplate.opsForValue().set(timeId, sendTime.toString().substring(0, 26)); // 2023-10-25T21:20:20.181231 형식의 마지막 나노초 단위에서 6자리로 맞춰야지 나중에 불러올 때도 formatting할 때
                                                                                            // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"); 형식에 맞춰서 불러올 수 있음.
        }
    }
   ```
   * (gif를 넣을 곳)
  ##### 4-4. 마지막 채팅 시간(~분 전, ~일 전, redis)
   > RedisService.java 일부
   ```java
    public class RedisService {
        // redis에 lastTime 캐시를 설정해둠.
        private void setCacheLastTime(Integer chatId, String lastTime) {
            log.info("cacheLastTime()");
            String id = "lastTime. chatId: " + chatId;
            redisTemplate.opsForValue().set(id, lastTime);
        }
    
        // redis cache 중 lastTime을 가져옴.
        private String getLastChatTimeFromCache(Integer chatId) {
            log.info("getLastTimeFromCache()");
            String id = "lastTime. chatId: " + chatId;
            return redisTemplate.opsForValue().get(id);
        }
        
        /**
         * 1. redis lastTime 캐시 데이터를 확인 후 없으면 2. jpaRepository에서 찾아옴 + lastTime 캐시에 저장(String).
         * @param chatId 마지막 시간을 가져올 chatId
         * @return 마지막 시간(LocalDateTime으로 전송-formatting으로 표현하기 위해)
         */
        @Transactional(readOnly = true)
        public LocalDateTime getLastTime(Integer chatId) {
            log.info("getLastTime(id={})", chatId);
            String lastTimeToString = getLastChatTimeFromCache(chatId);
            if(lastTimeToString == null) { 
                Optional<Message> message = Optional.ofNullable(messageRespository.findFirstByChatIdOrderByModifiedTimeDesc(chatId));
                if(message.isPresent()) {
                    lastTimeToString = message.get().getModifiedTime().toString();
                    setCacheLastTime(chatId, lastTimeToString);
                } else {
                    lastTimeToString = "";
                }
            }
            LocalDateTime lastTime = LocalDateTime.of(1111, 11, 11, 11, 11);
            if(!lastTimeToString.equals("")) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                lastTime = LocalDateTime.parse(lastTimeToString, format);
            } 
            return lastTime;
        }
    }
   ```
   > ChatController.java 일부
   ```java
    public List<ChatListDto> loadChatList(Integer userId){
        // lastTime 값이 있는지 없는지 비교해보기위한 기준 time
        LocalDateTime benchmarkTime = LocalDateTime.of(1111, 11, 11, 11, 11);
        for (Chat chat : chatList) {
                '
                '
                '
            LocalDateTime lastTimeBeforeFormat = redisService.getLastTime(chat.getId());
            String lastTime = "";
            if(!lastTimeBeforeFormat.isEqual(benchmarkTime)) { // 값이 없다면 formatting하면 안되기 때문에
                lastTime = TimeFormatting.formatting(lastTimeBeforeFormat);
            }
                '
                '
                '
        }
    }
   ```
   > TimeFormatting.java 일부
   ```java
    /**
     * LocalDateTime 시간을 입력하면 n분 전, n초 전을 리턴함.
     * @param writtenTime 대상 시간
     * @return ~시간 전의 문자열
     */
    public static String formatting(LocalDateTime writtenTime) {
        String msg = "";
        
        // 현재 시간
        LocalDateTime nowTime = LocalDateTime.now();
        
        // 현재시간 - 비교시간
        long diff = ChronoUnit.SECONDS.between(writtenTime, nowTime);
        
        if(diff == 0) {
            msg = TimeFormattingType.getValue(0);
        } else if(diff < TIME_STANDARD.SEC) {
            msg = diff + TimeFormattingType.getValue(1);
        } else if((diff /= TIME_STANDARD.SEC) < TIME_STANDARD.MIN) {
            msg = diff + TimeFormattingType.getValue(2);
        } else if((diff /= TIME_STANDARD.MIN) < TIME_STANDARD.HOUR) {
            msg = diff + TimeFormattingType.getValue(3);
        } else if((diff /= TIME_STANDARD.HOUR) < TIME_STANDARD.DAY) {
            msg = diff + TimeFormattingType.getValue(4);
        } else if((diff /= TIME_STANDARD.DAY) < TIME_STANDARD.MONTH) {
            msg = diff + TimeFormattingType.getValue(5);
        } else {
            msg = writtenTime.format(DateTimeFormatter.ofPattern("yy.MM.dd"));
        }
        
        return msg;
    }
   ```
   * (gif를 넣을 곳)
#### 5. 웹 서비스 운영을 위한 관리자와 유저들을 위한 편의 서비스
  ##### 5-1. 고객 지원 서비스/공지사항/신고 게시판
  - 고객 지원 서비스
   * (gif를 넣을 곳)
  - 공지사항
   * (gif를 넣을 곳)
  - 신고 게시판
   > report.js 일부
   ```js
function toggleTitle(id){
    const selectTitle = document.getElementById('detail' + id);
    if(selectTitle.style.display === 'block'){
        selectTitle.style.display = 'none';
    } else{
        selectTitle.style.display = 'block';        
    }
}
let vParagraph = ''; // 다음에 바뀔 문단
function toggleDetails(id) {
    // 선택된 부분을 보이게 해줌. 기존에 있던 부분은 안보이게 바꿔줌.
    if(vParagraph != ''){
        const deleteDetails = document.getElementById('detail' + vParagraph);
        deleteDetails.style.display = 'none';
    }
    const selectedDetails = document.getElementById('detail' + id);
    selectedDetails.style.display = 'block';
    vParagraph = id; // 다음에 바뀔 구문 저장해두기
    
}
   ```
   > report.html 일부
   ```html
<!-- left content -->
    <aside class="leftContent" style="width: 30%; height:1000px; border:1px solid red; float: left; margin-right: 5%;">
        <ul class="reportList list-group">
          <li class="list-group-item list-group-item-action" aria-disabled="true">
            <div class="detailTitles" onclick="toggleTitle(1)"><span>불량 유저 신고하기</span></div>
            <div class="detailItems" id="detail1">
              <p onclick="toggleDetails(11)">불량 유저 신고</p>
              <p onclick="toggleDetails(12)">금지 물품 판매 유저 신고</p>
              <p onclick="toggleDetails(13)">기타 유저 신고</p>
            </div>
          </li>
                                      '
                                      '
                                      '
    <!-- right content -->
    <section class="rightContent" style="width: 65%; height:1000px; float: right; border:1px solid green;">
        <div class="detailItems" id="detail11">
            <p class="detailItemsTitles">불량 유저 신고</p>
            <span>누군가 거래 도중 비매너, 불쾌한 언어, 욕설, 협박 등을 포함한 사기, 범죄 행위 등을 포함한 모든 행위를 신고할 수 있습니다.</span>
        </div>
        <div class="detailItems" id="detail12">
            <p class="detailItemsTitles">금지 물품 판매 유저 신고</p>
            <span>당근 나라는 현행 법령 상 판매가 허용되지 않는 불법 상품 및 유해 상품을 판매하는 행위를 제한합니다.
                  이를 위반할 경우 이에 대해서 신고할 수 있습니다.</span>
        </div>
                                        '
                                        '
                                        '
   ```
   * (gif를 넣을 곳)
  ##### 5-2. 개인 정보/프로필 이미지/비밀번호 변경 기능
  - 개인 정보 변경
   > mypage.html 일부
   ```html
        <div class="infoContent">
            <form id="formUpdate" method="post" action="/user/update">
                <div class="label"><i class="fa-solid fa-pencil"></i>이름</div>
                    <input class="form-control" type="text" id="name" name="name" placeholder="이름" th:value="${ u.name }" readOnly/>
                <div class="label"><i class="fa-solid fa-user-tag"></i>닉네임<span class="labelSpan" style="font-size: 22px; color: red;">*</span></div>
                    <input class="form-control" type="text" id="nickName" name="nickName" placeholder="사용할 닉네임" th:value="${ u.nickName }" required/>
                    <div>
                        <div id="nickAva" style="color: green; font-size: 8px; margin-left: 15px; display: none;">가능한 닉네임입니다.</div>
                        <div id="nickUnava" style="color: red; font-size: 8px; margin-left: 15px; display: none;">불가능한 닉네임입니다.</div>
                    </div>
                    <button type="button" class="nickCheckBtn w3-button w3-orange w3-round-large w3-hover-blue">중복 확인</button>
                <div class="label"><i class="fa-solid fa-phone"></i>휴대폰 번호<span class="labelSpan" style="font-size: 22px; color: red;">*</span></div>
                    <input class="form-control" type="tel" id="phone" name="phone" placeholder="010-1234-5678" required maxlength="13" th:value="${ u.phone }"/>
                                                    '
                                                    '
                                                    '
   ```
   > myPage.js 일부
   ```js
    // 닉네임 중복 확인 이벤트
    const nickCheckBtn = document.querySelector('.nickCheckBtn');
    const nickAva = document.getElementById('nickAva');
    const nickUnava = document.getElementById('nickUnava');
    nickCheckBtn.addEventListener('click', function(){
        const nickName = document.getElementById('nickName').value;
        axios.get('/user/nicknameChk', {
            params: {
                nickName: nickName
            }
        })
            .then(response => {
                if(response.data){
                    nickAva.style.display = "block";
                    nickUnava.style.display = "none";
                    alert("멋진 닉네임이네요!");
                    return;
                } else{
                    nickAva.style.display = "none";
                    nickUnava.style.display = "block";
                    alert("불가능한 닉네임이네요!");
                    return;
                }
            })
            .catch(error => { alert('오류체크 부탁드립니다.' + error)});
    });

    // 수정완료 버튼 이벤트
    const submitBtn = document.querySelector('.submitBtn');
    submitBtn.addEventListener('click', () =>{
        const nick = document.getElementById('nickName').value;
        const phone = document.getElementById('phone').value;
        const formUpdate = document.getElementById('formUpdate');
        const result = confirm('정말 수정하시겠습니까?')
        
        if(result){
            alert("정보 수정 완료를 위해 다시 로그인 해주세요!")
            if(nick != "" && phone != "" && phone.length == 13 
            && (nickAva.style.display === "block" || initialNickName === nick)){ // 필수 입력란이 비어있지 않고, 폰번호는 13자리이며, 닉네임 중복체크 통과한 경우(닉네임이 변함 없으면 무관)에만 제출함.
                formUpdate.action = '/user/update';
                formUpdate.method = 'post';
                formUpdate.submit();
            } else if(nick == "" || phone == "") {
                alert("빨간색으로 표시된 부분은 필수 입력 사항입니다.");
                return;
            } else if(phone.length != 13) {
                alert("휴대폰 번호를 정확히 입력해 주세요.");
                return;
            } else if(nickAva.style.display === "none"){
                alert("닉네임 중복 확인을 해주세요.");
                return;
            } 
        }
    });
   ```
   * (gif를 넣을 곳)
  - 프로필 이미지 변경
   > mypage.html 일부
   ```html
        <img class="imgUpdateBtn" src="/images/imageEditing.png" onclick="imgUpdate()"
        alt="프로필 수정하기 버튼" title="프로필 수정하기"/>

        <!-- 프로필 수정 모달 -->
        <div class="modal" id ="imageModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                     <div class="modal-header">
                         <i class="fa-solid fa-id-card-clip"></i><h5 class="modal-title">&nbsp;프로필 이미지</h5>
                         <button type="button" class="imgCloseBtn btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="profileForm" enctype="multipart/form-data" method="post" action="">
                           <img id="previewProfileImg" th:src="${ '/img/user/' + #authentication.principal.id }" style="width: 350px; height: 350px;"/>
                           <div style="color: maroon; font-size:12px;">
                                * 프로필 사진은 JPEG, JPG, PNG, JFIF 타입만 가능합니다. <br/>
                                * 프로필 사진의 크기에 따라서 조정이 될 수도 있습니다. <br/>
                                &nbsp;&nbsp;&nbsp;(최대 크기 제한 : 10MB)</div>
                           <label for="imgFile"><i class="fa-solid fa-upload" style="font-size: 24px;"></i></label>
                           <input style="display: none;" type="file" name="file" id="imgFile"/>
                        </form>
                    </div>
                    <div class="modal-footer">
                         <button type="button" id="imgChangeBtn" class="w3-button w3-orange w3-round-large w3-hover-blue">변경하기</button>
                    </div>
                 </div>
            </div>
        </div><!-- 프로필 수정 모달 END -->
   ```
   > myPage.js 일부
   ```js
window.addEventListener('DOMContentLoaded', () => {
    // 변경할 프로필 이미지 미리보기
    imgInput.addEventListener('change', (event) => {
        const previewImg = event.target.files[0];
        
        if (previewImg) {
        // file 미리보기 위해 파일리더를 이용
        const reader = new FileReader();
        reader.onload = function(event) {
            previewProfileImg.src = event.target.result;
        };
        reader.readAsDataURL(previewImg);
        }
    })
    
    // 프로필 이미지 변경 완료 이벤트
    imgChangeBtn.addEventListener('click', function(){
        const imgFile = document.querySelector('#imgFile');
        /* FormData로 전송하면 서버측에서 multipartFile로 받아서 처리
        이미지 파일을 FormData에 넣는 이유는 HTTP 요청을 보낼 때 파일 업로드와 같은 멀티파트 요청을 생성하기 위해서
        일반적으로 웹 애플리케이션에서 파일을 업로드하거나 다른 형태의 바이너리 데이터를 전송해야 할 때, 이 데이터를 멀티파트 형식으로 인코딩하여 서버로 전송
        */
        const file = new FormData();
        file.append('file', imgFile.files[0]);
        axios.delete('/img/upload/' + userImage)
            .then(response => {
                console.log("원래 이미지는 로컬에서 삭제됨.");
            })
            .catch(error => {
                console.log(error);
            });
        axios.post('/img/upload/profile', file)
            .then(response => { 
                alert('프로필 이미지 변경 완료했습니다.');
                imgCloseBtn.click();
                location.reload();
                })
            .catch(error => {
                alert('파일 확장자는 jpeg, jpg, png, jfif 타입만 가능하며 최대 10MB을 넘지 않는지 확인해 주세요.')
                console.log(error)
                });
    });
});
    const imageModal = document.querySelector('#imageModal');
    const imgModal = new bootstrap.Modal(imageModal);
    function imgUpdate(){
        imgModal.show();
    }
   ```
  - 비밀번호 변경
   > mypage.html 일부
   ```html
        <img class="pwUpdateBtn" src="/images/pwChange.png" onclick="pwUpdate()"
        alt="비밀번호 변경하기 버튼" title="비밀번호 변경하기"/>
        <!-- 비밀번호 변경 모달 -->
        <div class="modal" id ="pwModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                     <div class="modal-header">
                          <i class="fa-solid fa-unlock-keyhole"></i><h5 class="modal-title">&nbsp;비밀번호 변경</h5>
                         <button type="button" class="pwCloseBtn btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="pwForm" enctype="multipart/form-data" method="post" action="">
                           <div class="formLabel">현재 비밀번호</div>
                           <input type="password" name="nowPassword" id="nowPassword" placeholder="현재 비밀번호" required/>
                           <div class="formLabel">새로운 비밀번호</div>
                           <input class="passwordInput" type="password" id="password" name="password" placeholder="새로운 비밀번호" required/>
                           <div class="formLabel">비밀번호 확인</div>
                           <input class="passwordInput" type="password" id="passwordChk" name="passwordChk" placeholder="비밀번호 확인" required/>
                           <div id="pwAvailable" style="color: green; margin-left: 8px; display: none;">비밀 번호가 일치합니다.</div>
                           <div id="pwUnavailable" style="color: red; margin-left: 8px; display: none;">비밀 번호가 일치하지 않습니다.</div>
                        </form>
                    </div>
                    <div class="modal-footer">
                         <button type="button" id="pwChangeBtn" class="w3-button w3-orange w3-round-large w3-hover-blue" disabled>변경하기</button>
                    </div>
                 </div>
            </div>
        </div><!-- 비밀번호 변경 모달 END -->
   ```
   > myPage.js 일부
   ```js
window.addEventListener('DOMContentLoaded', () => {
    const nowPw = document.querySelector('#nowPassword');
    const newPw = document.querySelector('#password');
    const newPwChk = document.querySelector('#passwordChk');
    const pwChangeBtn = document.querySelector('#pwChangeBtn'); // 비밀번호 변경완료 버튼
    const pwModalCloseBtn = document.querySelector('.pwCloseBtn'); // 모달창 종료 버튼
    
    
    // 새로운 비밀번호 일치 여부
    newPw.addEventListener('input', isCorrect);
    newPwChk.addEventListener('input', isCorrect);
    const pwAva = document.querySelector('#pwAvailable');
    const pwUnava = document.querySelector('#pwUnavailable');
    function isCorrect(){
        if(newPw.value != "" && newPwChk.value != "" && newPw.value === newPwChk.value){
            pwAva.style.display = "block";
            pwUnava.style.display = "none";
        } else {
            pwAva.style.display = "none";
            pwUnava.style.display = "block";
        }
        isCorrect // 비밀번호 일치여부를 상시 체크하기 위해 실행.
    }
    
    // 비밀번호 변경 버튼 활성화(둘 다 일치할 경우만 활성화)
    newPw.addEventListener('focus', activePwBtn);
    newPwChk.addEventListener('focus', activePwBtn);
    newPw.addEventListener('blur', activePwBtn);
    newPwChk.addEventListener('blur', activePwBtn);
    function activePwBtn(){
        if(pwAva.style.display === "block"){
            pwChangeBtn.disabled = false;
        } else{
            pwChangeBtn.disabled = true;
        }
    }
    
    // 비밀번호 변경 완료 이벤트
    pwChangeBtn.addEventListener('click', function(){
        const data = {
            'nowPw': nowPw.value,
            'newPw': newPw.value
        };
        axios.post('/user/pwUpdate', data)
        .then(response => {
            if(response.data){
                alert('비밀번호를 변경했습니다. 다시 로그인 해주세요.');
                pwModalCloseBtn.click();
                location.href="/logout" // 로그아웃하지 않으면 DB에 새로 변경된 값이 저장되더라도 이전 값이 계속 적용됨.
            } else{
                alert('현재 비밀번호가 일치하지 않거나 새로운 비밀번호가 현재 비밀번호와 동일합니다.')
            }
        })
        .catch(error => {console.log(error)});
    });
});
    const passwordModal = document.querySelector('#pwModal');
    const pwModal = new bootstrap.Modal(passwordModal);
    function pwUpdate(){
        pwModal.show();
    }
   ```
#### 6. 지도 API를 이용한 검색(추정)

#### 7. 유저들 간의 자유 커뮤니케이션(추정)

#### 8. (희망)파이썬을 이용한 인공지능 기능

