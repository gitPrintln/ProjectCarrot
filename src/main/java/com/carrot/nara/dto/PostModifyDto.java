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
public class PostModifyDto {

    private Integer id;
    private String title;
    private String category;
    private String prices;
    private String content;
    private String region;
    private String status;
    
    private List<Integer> imgIds;
    
    public Post toEntity(Integer userId) {
        
        return Post.builder().id(id).title(title).
                category(category).prices(prices).content(content).region(region).status(status).
                build();
    }
}
