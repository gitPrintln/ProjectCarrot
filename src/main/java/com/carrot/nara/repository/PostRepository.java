package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.carrot.nara.domain.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT p.id FROM POST p ORDER BY p.id desc", nativeQuery = true)
    List<Integer> findRecentPostIds();
}
