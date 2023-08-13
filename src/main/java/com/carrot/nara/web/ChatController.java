package com.carrot.nara.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PostMapping;
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
    @Transactional
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/chat")
    public String chat(@AuthenticationPrincipal UserSecurityDto userDto, Integer chatId, Model model) {
        log.info("get-chat(user={},{})", userDto.getNickName(), chatId);
        Integer userId = userDto.getId();
        
        // 내가 속해 있는 모든 대화 채팅 목록(어디서 채팅방으로 들어가던지 공통)
        List<ChatListDto> list = new ArrayList<>(); // chat 목록에 사용될 최종 list
        List<Chat> chatList = chatService.myChatList(userId);
        if(chatList.size() > 0) { // 대화 목록 자료가 하나라도 있다면 최종 list에 담음
            for (Chat chat : chatList) {
                String sellerNick = userService.getNickName(chat.getSellerId());
                String sellerImage = userService.getImageName(chat.getSellerId());
                // TODO: seller image와 lastchat을 넣어줘야함. lastchat이나 그런건 redis로 가능하다던데
                ChatListDto entity = ChatListDto.builder()
                                    .id(chat.getId())
                                    .sellerId(chat.getSellerId()).sellerNickName(sellerNick).sellerImage(sellerImage)
                                    .lastTime(chat.getModifiedTime())
                                    .build();
                list.add(entity);
            }
            model.addAttribute("chatList", list);
        }
        
        // (1) 상단 채팅 버튼으로 연결하는 경우(chatId가 널이고 그냥 채팅 페이지 연결만.)
        if(chatId == null) {
            // 현재 대화 section에 사용될 상단 정보
            if(list.size() > 0) { // 최종 list에 대화 목록이 있을 경우 실행
                log.info("상단채팅 버튼으로 연결할 경우 대화내역 하나라도 있을 경우");
                chatId = list.get(0).getId();
                Integer postId = chatService.getPostId(chatId);
                Post post = postService.readByPostId(postId);
                PostImage pi = postService.readThumbnail(postId);
                
                CurrentChatDto topNowChat = CurrentChatDto.builder()
                        .id(chatId)
                        .sellerId(list.get(0).getSellerId()).sellerNickName(list.get(0).getSellerNickName()).sellerImage(list.get(0).getSellerImage())
                        .postId(postId).title(post.getTitle()).prices(post.getPrices()).region(post.getRegion())
                        .imageFileName(pi.getFileName())
                        .build();
                
                model.addAttribute("currentChat", topNowChat);
                // Graceful Degradation(우아한 저하) 원칙
                // Thymeleaf 템플릿에서는 model.addAttribute()를 통해 전달받은 데이터를 출력할 때, 해당 데이터가 없는 경우에도 일반적으로 오류가 발생 x
            }
            log.info("상단채팅 버튼으로 연결할 경우 대화내역 하나도 없을 경우.");
            return "chat";
        }
        
        
        
        // (2) post글의 detail에서 채팅창으로 연결하는 경우(chatId로 찾아옴)
        log.info("chat(chatInfo={},{})", userId, chatId);
        
        // chatId로 chatInfo 불러옴(postId, sellerId)
        Chat chatByDetail = chatService.loadChat(chatId);
        Integer postId = chatByDetail.getPostId();
        Integer sellerId = chatByDetail.getSellerId();
        
        // postInfo, sellerInfo 찾음
        Post post = postService.readByPostId(postId);
        PostImage pi = postService.readThumbnail(postId);
        String nowSellerNick = userService.getNickName(sellerId);
        String nowSellerImage = userService.getImageName(sellerId);
        
        CurrentChatDto nowChat = CurrentChatDto.builder()
                .id(chatId)
                .sellerId(sellerId).sellerNickName(nowSellerNick).sellerImage(nowSellerImage)
                .postId(postId).title(post.getTitle()).prices(post.getPrices()).region(post.getRegion())
                .imageFileName(pi.getFileName())
                .build();

        model.addAttribute("currentChat", nowChat);
        return "chat";
    }
    
    /**
     * userId, postId, sellerId 를 통해 chatId를 찾아오거나, 새로 생성함
     * @param userDto
     * @param postId
     * @param sellerId
     * @return chatId로 채팅 연결
     */
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    @PostMapping("/chat")
    public String chat(@AuthenticationPrincipal UserSecurityDto userDto, Integer postId, Integer sellerId) {
        log.info("post-chat({},{},{})", userDto.getId(),postId,sellerId);
        Integer userId = userDto.getId();
        
        Integer chatId = null; // 연결을 하게 될 chatId
        
        Optional<Chat> isPresentLoadedChat = Optional.ofNullable(chatService.loadChat(userId, postId, sellerId));
        if(isPresentLoadedChat.isPresent()) { // 불러온 채팅이 이미 존재하는 경우
            log.info("post글의 detail에서 채팅창으로 연결하는 경우 대화내역 이미 존재하는 경우");
            Chat loadedChat = isPresentLoadedChat.get();
            chatId = loadedChat.getId();
            return "/chat?chatId="+chatId;
        } 
        
        // 새로운 채팅을 생성해주는 경우
        log.info("post글의 detail에서 채팅창으로 연결하는 경우 대화내역 없어서 생성해줄 경우");
        Chat newChatInfo = chatService.createNewChat(userId, postId, sellerId);
        chatId = newChatInfo.getId();
        // 민감한 정보나 중요한 작업에 대한 처리를 할 때, 보안적인 측면을 고려하여 POST 요청을 사용하고 결과를 리다이렉트로 전달하는 것은 좋은 선택이지만
        // 민감한 정보가 URL에 노출되지 않도록 POST 요청을 사용하는 것은 보안 측면에서 중요.
        // 비효율적이더라도 안전한 방법으로 처리하는 것이 항상 바람직함.(보안성 높아짐)
        return "/chat?chatId="+chatId;
        // return "/chat?chatId=" + chatId;을 사용하는 경우, 클라이언트는 "/chat?chatId=123"과 같은 URL로 이동하는 것으로 간주될 수 있음.
        // 이 URL로 클라이언트에게 리다이렉트 응답을 보냄.
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
