package com.carrot.nara.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity(name = "USERS")
@SequenceGenerator(name = "USERS_SEQ_GEN", sequenceName = "USERS_SEQ", initialValue = 1, allocationSize = 1)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ_GEN")
    private Integer id;  //PK

    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String password;
    
    private String email;
    
    @Column(unique = true, nullable = false)
    private String phone;
    
    private String address;
    
    @Column(nullable = false)
    private String nickName;
    
    @Setter
    @Column(length = 1000)
    private String userImage; // 유저 이미지 저장된 경로
    
    // USER_ROLES 테이블이 생성(컬럼: USER_ID, ROLES)
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default // 초기값이 설정되어 있으면 null이 되지 않음.
    private Set<UserRole> roles = new HashSet<>();  // 권한 부여를 위한 해시셋
    
    public User addRole(UserRole role) { // 유저 권한 부여
        roles.add(role);
        
        return this;
    }
    
    public User clearRoles() { // 유저가 가진 모든 권한 삭제
        roles.clear();
        
        return this;
    }
    
    public User updatePassword(String password) { // 유저의 비밀 번호 변경
        this.password = password;
        return this;
    }
    
    public User updateUserImage(String userImage) { // 유저의 이미지 변경
        this.userImage = userImage;
        return this;
    }
}
