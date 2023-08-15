package com.carrot.nara.dto;

import com.carrot.nara.domain.Message;
import com.carrot.nara.domain.TimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
@Setter
public class MessageCreateDto extends TimeEntity {
    
    private Integer chatId;
    private String senderNickName;
    private String message;
    private String sendTime;
    
    public Message toEntity(Integer chatId) {
        return Message.builder().chatId(chatId).senderNickName(senderNickName).message(message).build();
    }
}
