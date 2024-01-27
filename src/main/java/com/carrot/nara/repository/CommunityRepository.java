package com.carrot.nara.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrot.nara.domain.Community;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

    // 카테고리에 따른 DB 데이터 가져오기
    List<Community> findByCategory(String category);

}
