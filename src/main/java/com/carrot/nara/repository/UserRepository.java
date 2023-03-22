package com.carrot.nara.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    
}
