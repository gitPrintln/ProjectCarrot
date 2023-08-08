package com.carrot.nara.dto;

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
public class MessageReadDto {
    
    private String sender;
    private String message;
    private String sendTime;
    
    public MessageReadDto() {}
}
