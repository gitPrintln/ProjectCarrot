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

    private Integer sellerId;
    private String sellerImage;
    private String lastChat;
    private LocalDateTime lastTime;
}
