package com.carrot.nara.dto;

import org.springframework.data.domain.Page;

import com.carrot.nara.domain.Community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
@Data
public class BoardListDto {

    private String category;
    private Integer currentPage;
    private Integer startPage;
    private Integer endPage;
    private Integer totalPages;
    private Page<Community> entity; 
}
