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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity(name = "COMMUNITY")
@SequenceGenerator(name = "COMMUNITY_SEQ_GEN", sequenceName = "COMMUNITY_SEQ", initialValue = 1, allocationSize = 1)
public class Community extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMUNITY_SEQ_GEN")
    private Integer id;  //PK

    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = true)
    private String type; // 문의글인 경우 문의 유형
    
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;
}
