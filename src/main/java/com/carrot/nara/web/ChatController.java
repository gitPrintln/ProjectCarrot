package com.carrot.nara.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carrot.nara.dto.MessageReadDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String chat() {
        log.info("chat()");
        return "chat";
    }
    
    /**
     * 메세지 컨트롤러
     * @param userId 대화하고 있는 상대
     * @throws IOException
     */
    @MessageMapping("/{partner}")
    public void send(@DestinationVariable Integer partner, MessageReadDto dto) throws IOException{
        log.info("send(dto={}, {}, {})", dto.getSender(), dto.getMessage(), dto.getSendTime());
        String url = "/user/queue/messages";
        simpMessagingTemplate.convertAndSend(url, new MessageReadDto(dto.getSender(), dto.getMessage(), dto.getSendTime()));
    }
    
    
}
