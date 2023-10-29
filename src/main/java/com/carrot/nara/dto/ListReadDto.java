package com.carrot.nara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class ListReadDto {
    
    private Integer id;
    private String imageFileName;
//    private String imageFilePath; // 굳이 filepath가 필요할까?
    private String title;
    private String region;
    private String prices;
    private Integer chats;
    private Integer wishCount;
    private Integer hits;
    private String status;
    private String modifiedTime;
}
