package com.malzzang.tgtg.chatroom.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.chatroom.dto.ChatMessage;
import com.malzzang.tgtg.chatroom.service.ChatroomService;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.chatroom.service.ReadyUserService;
import com.malzzang.tgtg.common.GameRoleDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
	
	private final ReadyUserService readyUserService;
	private final ConnectedUserService connectedUserService;
	private final AnonymousService anonymousService;
	
	@Autowired
	ChatroomService chatroomService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	//메시지 전송 메소드
	@MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage chat(@DestinationVariable int roomId, ChatMessage message) {

        return ChatMessage.builder()
                .roomId(roomId)
                .sender(message.getSender())
                .senderEmail(message.getSenderEmail())
                .senderImage(message.getSenderImage())
                .message(message.getMessage())
                .build();
	} 
	
	//게임 준비 메소드
	@MessageMapping("/{roomId}/ready")
    @SendTo("/room/{roomId}/getReady")
    public int ready(@DestinationVariable int roomId) {
        readyUserService.readyUser(roomId);
        if(readyUserService.getReady(roomId) == connectedUserService.getConnectedUserCount(roomId) && 
        		readyUserService.getReady(roomId)>= 3	) {
        	List<AnonymousDTO> list = chatroomService.startGame(roomId);
        	
        	GameRoleDTO role = new GameRoleDTO();
        	role.setRoleList(list);
        	if(roomId < 100) {  
        		role.setUrl("/user/textGame?roomId=");        		simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", "/user/textGame?roomId=");
        	}
        	else {
        		role.setUrl("/user/voiceGame?roomId=");        		simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", "/user/voiceGame?roomId=");
        	}
        	simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", role);
        }
        return readyUserService.getReady(roomId);
    }
	
	//게임 준비취소 메소드
	@MessageMapping("/{roomId}/unready")
    @SendTo("/room/{roomId}/getReady")
    public int unready(@DestinationVariable int roomId) {
        readyUserService.unreadyUser(roomId);
        return readyUserService.getReady(roomId);
    }
	
	//현재 준비한 회원 수
	@MessageMapping("/{roomId}/getReadyCount")
	@SendTo("/room/{roomId}/getReady")
	public int getReady(@DestinationVariable int roomId) {
	    return readyUserService.getReady(roomId);
	}
	
	//회원이 게임 대기방 들어왔을 때
	@MessageMapping("/{roomId}/enter")
    @SendTo("/room/{roomId}/connect")
    public Map enter(@DestinationVariable int roomId, AnonymousDTO anonymous) {
		
		//인원수 추가해줌
        connectedUserService.userEntered(roomId, anonymous);
        
        //해당방 회원
        List<AnonymousDTO> list = connectedUserService.getAllMembersInRoom(roomId);
        
        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", true);
        map.put("memberList", list);
        
        return map;
    }

	//회원이 게임 대기방 퇴장했을 때
    @MessageMapping("/{roomId}/leave")
    @SendTo("/room/{roomId}/connect")
    public Map leave(@DestinationVariable int roomId, AnonymousDTO anonymous) {

        connectedUserService.userLeft(roomId, anonymous);
        anonymousService.deleteAnonymous(anonymous.getAnonymousId());
        
        //해당방 회원
        List<AnonymousDTO> list = connectedUserService.getAllMembersInRoom(roomId);
        
        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", false);
        map.put("memberList", list);
        
        return map;
    }
    
    //게임방 메시지 전송 메소드
  	@MessageMapping("/{roomId}/startTime")
	@SendTo("/room/{roomId}/timer")
	public String gameChat(@DestinationVariable int roomId) {
  		System.out.println("입장완");
  		String word = "타이머 시작함";
  		return word;
  	}
  	
  	@MessageMapping("/peer/offer/{camKey}/{roomId}")
    @SendTo("/room/peer/offer/{camKey}/{roomId}")
    public String PeerHandleOffer(@Payload String offer, @DestinationVariable(value = "roomId") int roomId,
                                  @DestinationVariable(value = "camKey") String camKey) {
        log.info("[OFFER] {} : {}", camKey, offer);
        return offer;
    }
    
	//iceCandidate 정보를 주고 받기 위한 webSocket
	//camKey : 각 요청하는 캠의 key , roomId : 룸 아이디
	@MessageMapping("/peer/iceCandidate/{camKey}/{roomId}")
	@SendTo("/room/peer/iceCandidate/{camKey}/{roomId}")
	public String PeerHandleIceCandidate(@Payload String candidate, @DestinationVariable(value = "roomId") int roomId,
	                                     @DestinationVariable(value = "camKey") String camKey) {
	    log.info("[ICECANDIDATE] {} : {}", camKey, candidate);
	    return candidate;
	}
	
    @MessageMapping("/peer/answer/{camKey}/{roomId}")
    @SendTo("/room/peer/answer/{camKey}/{roomId}")
    public String PeerHandleAnswer(@Payload String answer, @DestinationVariable(value = "roomId") int roomId,
                                   @DestinationVariable(value = "camKey") String camKey) {
        log.info("[ANSWER] {} : {}", camKey, answer);
        return answer;
    }
    
    //camKey 를 받기위해 신호를 보내는 webSocket
    @MessageMapping("/call/key")
    @SendTo("/room/call/key")
    public String callKey(@Payload String message) {
        log.info("[Key] : {}", message);
        return message;
    }
		
	//자신의 camKey 를 모든 연결된 세션에 보내는 webSocket
    @MessageMapping("/send/key")
    @SendTo("/room/send/key")
    public String sendKey(@Payload String message) {
        return message;
    }
}
