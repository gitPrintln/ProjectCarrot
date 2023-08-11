package com.carrot.nara.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carrot.nara.domain.Chat;
import com.carrot.nara.domain.Post;
import com.carrot.nara.domain.PostImage;
import com.carrot.nara.dto.ChatListDto;
import com.carrot.nara.dto.CurrentChatDto;
import com.carrot.nara.dto.MessageReadDto;
import com.carrot.nara.dto.UserSecurityDto;
import com.carrot.nara.service.ChatService;
import com.carrot.nara.service.PostService;
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
    @Autowired
    private PostService postService;
    
    
    // 주의: @GetMapping, @PostMapping 에서 @RequestMapping이 공유 되지만 @MessageMapping에서는 공유 안됨.
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chat")
    public String chat(@AuthenticationPrincipal UserSecurityDto userDto, Integer postId, Integer sellerId, Model model) {
        log.info("chat(user={},{})", userDto.getNickName(), userDto.getUsername());
        
        // (1) 상단 채팅 버튼으로 연결하는 경우(postid, sellerid가 널임.)
        if(postId == null) {
        // 내가 속해 있는 모든 대화 채팅 목록
        List<ChatListDto> list = new ArrayList<>(); // chat 목록에 사용될 list
        List<Chat> chatList = chatService.myChatList(userDto.getId());
        for (Chat chat : chatList) {
            String sellerImage = userService.getImageName(chat.getSellerId());
            String sellerNick = userService.getNickName(chat.getSellerId());
            // TODO: seller image와 lastchat을 넣어줘야함. lastchat이나 그런건 redis로 가능하다던데
            ChatListDto entity = ChatListDto.builder()
                                .id(chat.getId())
                                .sellerId(chat.getSellerId()).sellerNickName(sellerNick).sellerImage(sellerImage)
                                .lastTime(chat.getModifiedTime())
                                .build();
            list.add(entity);
        }
        model.addAttribute("chatList", list);
        
        // 현재 대화 section에 사용될 상단 정보
        if(list.size() > 0) {
            Integer chatId = list.get(0).getId();
            postId = chatService.getPostId(chatId);
            Post post = postService.readByPostId(postId);
            PostImage pi = postService.readThumbnail(postId);
            CurrentChatDto nowChat = CurrentChatDto.builder()
                    .id(chatId)
                    .sellerId(list.get(0).getSellerId()).sellerNickName(list.get(0).getSellerNickName()).sellerImage(list.get(0).getSellerImage())
                    .postId(postId).title(post.getTitle()).prices(post.getPrices()).region(post.getRegion())
                    .imageFileName(pi.getFileName())
                    .build();
            model.addAttribute("currentChat", nowChat);
            // Graceful Degradation(우아한 저하) 원칙
            // Thymeleaf 템플릿에서는 model.addAttribute()를 통해 전달받은 데이터를 출력할 때, 해당 데이터가 없는 경우에도 일반적으로 오류가 발생 x
        }
            return "chat";
        }
        
        // (2) post글의 detail에서 채팅창으로 연결하는 경우.
        log.info("chat(chatInfo={},{},{})", userDto.getId(), postId, sellerId);
        
        
        
        
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
