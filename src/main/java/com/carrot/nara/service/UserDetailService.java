package com.carrot.nara.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.carrot.nara.domain.User;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
