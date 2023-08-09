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
@Entity(name = "CHAT")
@SequenceGenerator(name = "CHAT_SEQ_GEN", sequenceName = "CHAT_SEQ", initialValue = 1, allocationSize = 1)
public class Chat extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_SEQ_GEN")
    private Integer id;

    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private Integer sellerId;
    
    @Column(nullable = false)
    private Integer postId;
    
}
