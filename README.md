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
   - 이미지 업로드 편의 기능
   > sell.html 일부
   ```java
        <div><input type="file" style="display: none;" class="w3-input w3-border w3-sand" id="images" name="images" multiple accept=".jpg, .jpeg, .png, .jfif"/></div>
        </div>
        <!-- 선택한 파일이 보여지는 공간 -->
        <div id="selectedImage"></div>
   ```
   * 로컬 저장소에 저장하는 시점과 DB에 이미지 파일을 불러오는 시점을 분리해뒀기 때문에 사용자 편의를 위해 업로드 후 드래그로 여러 장 업로드, 추가 업로드, 추가 삭제가 가능함.
   * (gif를 넣을 곳)
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
   * (gif를 넣을 곳)
#### 3. 거래를 희망하는 유저들간의 1:1 채팅(Stomp websocket, Redis)
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
   > chat.js 일부
   ```java





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
#### 4. 웹 서비스 운영을 위한 관리자와 유저들을 위한 편의 서비스

#### 5. 지도 API를 이용한 검색(추정)

#### 6. 유저들 간의 자유 커뮤니케이션(추정)

#### 7. (희망)파이썬을 이용한 인공지능 기능

