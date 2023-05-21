package com.carrot.nara.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
    
    // 외래키 postId, 이미지 테이블에 저장 후 postid를 생성하기 때문에 null 가능하도록 설정.
    // 그냥 integer 타입으로 서로 연결지어서 찾도록 했음.
    // ManyToOne 사용하려면, ORM의 이점을 이용하기 위해서 관계를 명확히 해주는 것이 좋음. 
    @Column
    private Integer postId;
    
    @Column
    private String originFileName; // 원래 파일 이름
    
    @Column
    private String fileName; // UUID_원래파일이름
    
    @Column
    private String filePath; // 로컬 저장소 경로
    
    public PostImage update(Integer postId) {
        this.postId = postId;
        return this;
    }
}
