package com.carrot.nara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class CurrentChatDto {

    private Integer id; // 채팅방의 id
    private Integer partnerId; // 판매자와 대화중인 상대방의 id
    private Integer sellerId; // 판매자의 id
    private String sellerNickName; // 판매자의 닉네임
    private String partnerNickName; // 대화중인 상대방의 닉네임
//  private String sellerImage; // 바로 user정보에서 불러오기 때문에 굳이 넣을 필요 x
    
    private Integer postId;
    private String title;
    private String prices;
    private String region;
    
    private String imageFileName;
}
