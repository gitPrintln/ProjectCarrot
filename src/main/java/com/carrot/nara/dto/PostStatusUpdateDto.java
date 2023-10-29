package com.carrot.nara.dto;

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
public class PostStatusUpdateDto {
    
    private Integer id;
    private String status;

    public PostStatusUpdateDto() {
    }
}
