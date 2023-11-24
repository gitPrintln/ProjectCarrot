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
#### 2. 판매할 중고 상품 등록 및 수정, 삭제(이미지 여러장 등록, 카카오 위치 API)

#### 3. 최신 순으로 업데이트된 유저들의 거래 목록

#### 4. 거래를 희망하는 유저들간의 1:1 채팅(Stomp websocket)

#### 5. 웹 서비스 운영을 위한 관리자와 유저들을 위한 편의 서비스

#### 6. 지도 API를 이용한 검색(추정)

#### 7. 유저들 간의 자유 커뮤니케이션(추정)

#### 8. (희망)파이썬을 이용한 인공지능 기능

