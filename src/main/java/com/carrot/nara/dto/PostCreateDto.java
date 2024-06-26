package com.carrot.nara.dto;

import java.util.List;

import com.carrot.nara.domain.Post;

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
public class PostCreateDto {
    
    private Integer userId;
    private String title;
    private String category;
    private String prices;
    private String content;
    private String region;
    
    private List<Integer> imgIds;
    
    public Post toEntity(Integer userId) {
        
        return Post.builder().userId(userId).title(title).
                category(category).prices(prices).content(content).region(region).build();
    }
}
