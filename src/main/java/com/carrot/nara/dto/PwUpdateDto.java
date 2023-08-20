package com.carrot.nara.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PwUpdateDto {
    
    private String nowPw;
    private String newPw;
}
