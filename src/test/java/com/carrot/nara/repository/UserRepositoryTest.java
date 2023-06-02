package com.carrot.nara.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.carrot.nara.domain.User;
import com.carrot.nara.domain.UserRole;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    
//    @Test
    public void testUser() {
        User user1 = User.builder().username("user1").password("user1").name("admin1")
                .email("user1@test.com").phone("010-1111-2222").nickName("guest1").build();
        user1.addRole(UserRole.USER); // user 권한 부여
        userRepository.save(user1);
        
        User user2 = User.builder().username("user2").password("user2").name("admin2")
                .email("user2@test.com").phone("010-2222-3333").nickName("guest2").build();
        user2.addRole(UserRole.USER); // user 권한 부여
        user2.addRole(UserRole.ADMIN);// admin 권한 부여
        userRepository.save(user2);
        
        List<User> list = userRepository.findAll();
        for (User user : list) {
            log.info("user: {}", user.toString());
        }
    }
    
//    @Test
    public void testFindByUsername() {
        User user = userRepository.findByUsername("user1").get();
        log.info("user={}", user);
        user.getRoles().forEach(x -> log.info(x.getRole()));
        
        User user2 = userRepository.findByUsername("user2").get();
        log.info("user2={}", user2);
        user2.getRoles().forEach(x -> log.info(x.getRole()));
    }
    
    @Test
    public void testClearRoles() {
        User user2 = userRepository.findByUsername("user2").get();
        log.info("user2={}", user2);
        user2.clearRoles();
        user2.getRoles().forEach(x -> log.info(x.getRole()));
        user2 = userRepository.save(user2);
        log.info("user2={}", user2);
    }
}
