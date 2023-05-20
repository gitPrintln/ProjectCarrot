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
@Getter
@Builder
@ToString
@Entity(name = "POSTIMAGE")
@SequenceGenerator(name = "POSTIMAGE_SEQ_GEN", sequenceName = "POSTIMAGE_SEQ", allocationSize = 1)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSTIMAGE_SEQ_GEN")
    private Integer id;
    
//    @Column(nullable = false)
//    private 
//    
//    @Column
//    private String originFileName;
//    
//    @Column
//    private String fileName;
//    
//    @Column
//    private String filePath;
}
