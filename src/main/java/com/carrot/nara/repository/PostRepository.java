package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrot.nara.domain.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT p.id FROM POST p ORDER BY p.modified_time desc", nativeQuery = true)
    List<Integer> findRecentPostIds();

    List<Post> findAllByOrderByModifiedTimeDesc();
    
    @Query(value = "UPDATE POST p SET p.hits = p.hits +1 WHERE p.id = :postId")
    @Modifying
    void upHits(@Param(value = "postId") Integer postId);
    
}
