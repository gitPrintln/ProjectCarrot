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
    
    // TODO: USER ID userID로 해놨는데 추후에 user 만들어지면 User user로 변경해서 넣을것.
    public Post toEntity(Integer userId) {
        
        return Post.builder().userId(userId).title(title).
                category(category).prices(prices).content(content).region(region).build();
    }
}
