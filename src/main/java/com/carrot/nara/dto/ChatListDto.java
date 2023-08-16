package com.carrot.nara.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class ChatListDto {

    private Integer id; // 채팅방의 id
    private Integer sellerId;
    private String sellerNickName;
    private String sellerImage;
    private String lastChat;
    private LocalDateTime lastTime;
    
}