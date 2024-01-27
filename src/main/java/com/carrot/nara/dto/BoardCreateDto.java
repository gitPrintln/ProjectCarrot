package com.carrot.nara.dto;

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
public class BoardCreateDto {

    private Integer userId;
    private String category;
    private String type;
    private String title;
    private String content;
    
    public Community toEntity() {
        
        return Community.builder()
                .category(category).type(type).title(title).content(content)
                .userId(userId).build();
    }
}
