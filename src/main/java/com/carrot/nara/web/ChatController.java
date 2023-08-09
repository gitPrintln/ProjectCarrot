package com.carrot.nara.web;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.carrot.nara.domain.Chat;
import com.carrot.nara.dto.MessageReadDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.ChatService;
import com.carrot.nara.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {
    
    // @Autowired는 의존성 주입에 사용되며, 스프링이 빈을 관리하는 컨테이너에서 생성된 빈을 필드, 생성자, 메서드 등에 주입
    // @RequiredArgsConstructor는 생성자를 자동으로 생성하는데 사용되며, 주로 final로 선언된 필드를 초기화하는 생성자를 자동으로 생성
    // @Autowired로 생성자를 통해 의존성을 주입하는 것이 가독성과 테스트 용이성 측면에서 권장하는 방식이다.
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    
    
    
    // 주의: @GetMapping, @PostMapping 에서 @RequestMapping이 공유 되지만 @MessageMapping에서는 공유 안됨.
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chat")
    public String chat(@AuthenticationPrincipal UserSecurityDto userDto, Model model) {
        log.info("chat(user={},{})", userDto.getNickName(), userDto.getUsername());
        List<Chat> chatList = chatService.myChatList(userDto.getId());
        model.addAttribute("chatList", chatList);
        // TODO: chatList에 해당하는 id마다 usernickname 불러와서 chatListDto 채우기 그리고 그것을 전달.
        
        return "chat";
    }
    
    /**
     * 메세지 컨트롤러
     * @param partner 대화하고 있는 상대
     * @throws IOException
     */
    @MessageMapping("/chat/{partner}")
    public void send(@DestinationVariable Integer partner, MessageReadDto dto) throws IOException{
        log.info("send(dto={}, {}, {})", dto.getSender(), dto.getMessage(), dto.getSendTime());
        String url = "/user/queue/messages";
        simpMessagingTemplate.convertAndSend(url, new MessageReadDto(dto.getSender(), dto.getMessage(), dto.getSendTime()));
    }
    
    
}
