package com.carrot.nara.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Builder
@Getter
@ToString
@Entity(name = "MESSAGE")
@SequenceGenerator(name = "MESSAGE_SEQ_GEN", sequenceName = "MESSAGE_SEQ", initialValue = 1, allocationSize = 1)
public class Message extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_SEQ_GEN")
    private Integer id;

    @Column(nullable = false)
    private Integer chatId;
    
    @Column(nullable = false, length = 255)
    private String senderNickName;
    
    @Column(nullable = false, length = 1000)
    private String message;

    @Builder.Default
    private Integer readChk = 1; // 읽었으면 0, 안읽었으면 1(default = 1)

    
}
