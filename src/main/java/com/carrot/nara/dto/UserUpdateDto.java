package com.carrot.nara.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserUpdateDto {
    
    private String nickName;
    private String phone;
    private String email;
    private String address;
    
}
