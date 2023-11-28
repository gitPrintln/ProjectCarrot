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
@Entity(name = "POSTLIKE")
@SequenceGenerator(name = "POSTLIKE_SEQ_GEN", sequenceName = "POSTLIKE_SEQ", initialValue = 1, allocationSize = 1)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSTLIKE_SEQ_GEN")
    private Integer id;  //PK

    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private Integer postId;
}
