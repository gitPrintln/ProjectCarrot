package com.carrot.nara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class ChatAlarmDto {

    private String userNick;
    private Integer userId;
    
    public ChatAlarmDto() {} // cannot deserialize from Object value (no delegate- or property-based Creator)
                             // json 타입으로 받아올 때 모델을 알아서 생성하지 못함.ㄴ
                             // 기본 생성자로 MessageConverter(스프링 부트)가 JSON을 Object로 변환을 해주기 때문
}
