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
@Entity(name = "POST")
@SequenceGenerator(name = "POST_SEQ_GEN", sequenceName = "POST_SEQ", initialValue = 1, allocationSize = 1)
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_SEQ_GEN")
    private Integer id;  //PK

    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private Integer prices;
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String region;
    
    // 판매 상태(판매중, 예약중, 판매완료)
    @Builder.Default
    private String status = "판매중";
    
    @Builder.Default
    private Integer wishCount = 0;
    
    @Builder.Default
    private Integer hits = 0;
    
    @Builder.Default
    private Integer chats = 0;
}
