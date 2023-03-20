package com.carrot.nara.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    private String address;
    
    @Column(unique = true, nullable = false)
    private String nickName;
    
    @Setter
    @Column(length = 1000)
    private String userImage;
    
    @Setter
    @Column(length = 1000)
    private String fileName;
    
    @Setter
    @Column(length = 1000)
    private String filePath;
}
