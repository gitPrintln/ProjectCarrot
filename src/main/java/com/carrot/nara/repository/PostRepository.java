package com.carrot.nara.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
