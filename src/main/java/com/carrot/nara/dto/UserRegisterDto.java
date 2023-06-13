package com.carrot.nara.dto;

import com.carrot.nara.domain.User;
import com.carrot.nara.domain.UserRole;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRegisterDto {
    
    private String username;
    private String password;
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private String address;
    
    public User toEntity() {
        return User.builder().username(username).password(password).name(name).nickName(nickName).phone(phone).email(email)
                .address(address).build().addRole(UserRole.USER);
    }
    
}
