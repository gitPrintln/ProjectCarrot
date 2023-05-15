package com.carrot.nara.dto;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class PostCreateDto {
    
    private Integer userId;
    private String title;
    private String category;
    private String prices;
    private String content;
    private String region;
    
    public Post toEntity(User user) {
        
        return Post.builder().userId(user.getId()).title(title).
                category(category).prices(prices).content(content).region(region).build();
    }
}
