package com.carrot.nara.dto;

import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.User;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString
public class PostUpdateDto {
    
    private Integer postId;
    private Integer userId;
    private String title;
    private String category;
    private String prices;
    private String content;
    private String region;
    private String status;
    
    public Post toEntity(User user) {
        
        return Post.builder().id(postId).userId(user.getId()).title(title).
                category(category).prices(prices).content(content).region(region).status(status).build();
    }
}
