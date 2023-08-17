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
//    private String sellerImage; // 바로 user정보에서 불러오기 때문에 굳이 넣을 필요 x
    private String lastChat;
    private LocalDateTime lastTime;
    
}
