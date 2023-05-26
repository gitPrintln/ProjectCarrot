package com.carrot.nara.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class ListReadDto {
    
    private String imageFileName;
    private String imageFilePath;
    private String title;
    private String region;
    private String prices;
    private LocalDateTime modifiedTime;
}
