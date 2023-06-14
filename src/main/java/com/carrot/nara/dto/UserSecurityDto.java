package com.carrot.nara.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserSecurityDto extends User {

    private Integer id;
    private String username;
    private String password;
    private String nickName;
    
    public UserSecurityDto(Integer id, String username, String password, String nickName,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickName = nickName;
    }
    
    public static UserSecurityDto fromEntity(com.carrot.nara.domain.User u) {
        List<GrantedAuthority> authorities = u.getRoles().stream()
                .map(x -> new SimpleGrantedAuthority(x.getRole()))
                .collect(Collectors.toList());
        UserSecurityDto dto = new UserSecurityDto(u.getId(), u.getUsername(),
                u.getPassword(), u.getNickName(), authorities);
        return dto;
    }
}
