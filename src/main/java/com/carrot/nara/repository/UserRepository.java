package com.carrot.nara.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 사용자 로그인 아이디가 일치하는 정보가 있는지 검색
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);
}
