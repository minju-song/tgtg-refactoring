package com.malzzang.tgtg.chatroom.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
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
@EnableAsync
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
	
	//메시지 전송 메소드
	@MessageMapping("/{roomId}/game") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}/game")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage gameChat(@DestinationVariable int roomId, ChatMessage message) {
		
		ChatMessage msg = ChatMessage.builder()
                .roomId(roomId)
                .sender(message.getSender())
                .senderEmail(message.getSenderEmail())
                .senderImage(message.getSenderImage())
                .message(message.getMessage())
                .build();
		
		msg.setGameRole(message.getGameRole());
        return msg;
	} 
	
	//게임 준비 메소드
	@MessageMapping("/{roomId}/ready")
    @SendTo("/room/{roomId}/getReady")
    public int ready(@DestinationVariable int roomId) {
		//준비인원 추가
        readyUserService.readyUser(roomId);
        //준비한 인원 수가 방에 접속한 인원 수와 같고, 접속한 인원 수가 3명 이상이면 게임 시작
        if(readyUserService.getReady(roomId) == connectedUserService.getConnectedUserCount(roomId) && 
        		readyUserService.getReady(roomId)>= 3	) {
        	//해당 방의 상태를 'run'으로 업데이트해주고, 주제 및 팀을 랜덤으로 select
        	//해당 방에 접속한 익명 프로필 리스트를 return 받음
        	Set<AnonymousDTO> memberList = chatroomService.startGame(roomId);
        	
        	//해당 방에 접속한 리스트에게 각각 역할을 부여해줌 (심판/A팀/B팀)
        	GameRoleDTO role = new GameRoleDTO();
        	role.setRoleList(memberList);
        	//텍스트 게임방 url
        	if(roomId < 100) {  
        		role.setUrl("/user/textGame?roomId=");
        	}
        	//음성 게임방 url
        	else {
        		role.setUrl("/user/voiceGame?roomId=");
        	}
        	
        	startGame(roomId, role);
        	//simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", role);
        }
        return readyUserService.getReady(roomId);
    }
	
	@Async
	public void startGame(int roomId, GameRoleDTO role) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	    scheduler.schedule(() -> {
	        simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", role);
	    }, 1, TimeUnit.SECONDS);
	    scheduler.shutdown();
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
        Set<AnonymousDTO> memberList = connectedUserService.getAllMembersInRoom(roomId);
        
        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", true);
        map.put("memberList", memberList);
        
        return map;
    }

	//회원이 게임 대기방 퇴장했을 때
    @MessageMapping("/{roomId}/leave")
    @SendTo("/room/{roomId}/connect")
    public Map leave(@DestinationVariable int roomId, AnonymousDTO anonymous) {

        connectedUserService.userLeft(roomId, anonymous);
        anonymousService.deleteAnonymous(anonymous.getAnonymousId());
        
        //해당방 회원
        Set<AnonymousDTO> memberList = connectedUserService.getAllMembersInRoom(roomId);
        
        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", false);
        map.put("memberList", memberList);
        
        return map;
    }
    
    //게임방 타이머 현재시간
  	@MessageMapping("/{roomId}/sendTime")
	public void gameTimer(@DestinationVariable int roomId) {
  		System.out.println("입장완");
  		LocalTime endTime = connectedUserService.gameStartUser(roomId);
  		if(endTime != null) {
  			simpMessagingTemplate.convertAndSend("/room/" + roomId + "/sendTime", endTime);
  			System.out.println("다 들어옴" + endTime);
  		}
  	}
  	
  	//게임방 투표
  	@MessageMapping("/{roomId}/gameVote")
	public void gameVote(@DestinationVariable int roomId, int gameSelect) {
  		System.out.println("결과~~" + gameSelect);
  		connectedUserService.gameVoteCount(roomId, gameSelect);
  	}
  	
  //회원이 게임 대기방 퇴장했을 때
    @MessageMapping("/{roomId}/getResult")
    public void gameResult(@DestinationVariable int roomId) {
    	if(connectedUserService.getZeroCount(roomId)) {
    		String result = connectedUserService.getVoteResult(roomId);
//    		먼저 DB에 저장
    		System.out.println("결과 : "+result);
    		connectedUserService.saveResult(roomId, result);
//    		관련정보 삭제
    		connectedUserService.deleteRoom(roomId);
    		chatroomService.removeRoomById(roomId);
    		readyUserService.deleteReadyUser(roomId);
//    		결과전송
    		simpMessagingTemplate.convertAndSend("/room/" + roomId + "/getResult", result);
    	}
    }
  	
  	@MessageMapping("/peer/offer/{camKey}/{roomId}")
    @SendTo("/room/peer/offer/{camKey}/{roomId}")
    public String PeerHandleOffer(@Payload String offer, @DestinationVariable(value = "roomId") int roomId,
                                  @DestinationVariable(value = "camKey") String camKey) {
        log.info("[OFFER] {} : {}", camKey, offer);
        System.out.println(camKey+offer+">>>" );
        return offer;
    }
    
	//iceCandidate 정보를 주고 받기 위한 webSocket
	//camKey : 각 요청하는 캠의 key , roomId : 룸 아이디
	@MessageMapping("/peer/iceCandidate/{camKey}/{roomId}")
	@SendTo("/room/peer/iceCandidate/{camKey}/{roomId}")
	public String PeerHandleIceCandidate(@Payload String candidate, @DestinationVariable(value = "roomId") int roomId,
	                                     @DestinationVariable(value = "camKey") String camKey) {
	    log.info("[ICECANDIDATE] {} : {}", camKey, candidate);
	    System.out.println(camKey+candidate+">>>" );
	    return candidate;
	}
	
    @MessageMapping("/peer/answer/{camKey}/{roomId}")
    @SendTo("/room/peer/answer/{camKey}/{roomId}")
    public String PeerHandleAnswer(@Payload String answer, @DestinationVariable(value = "roomId") int roomId,
                                   @DestinationVariable(value = "camKey") String camKey) {
        log.info("[ANSWER] {} : {}", camKey, answer);
        System.out.println(camKey+answer+">>>" );
        return answer;
    }
    
    //camKey 를 받기위해 신호를 보내는 webSocket
    @MessageMapping("/call/key/{roomId}")
    @SendTo("/room/call/key/{roomId}")
    public String callKey(@Payload String message, @DestinationVariable(value = "roomId") int roomId) {
        log.info("[Key] : {}", message);
        System.out.println(">>>"+message);
        return message;
    }
		
	//자신의 camKey 를 모든 연결된 세션에 보내는 webSocket
    @MessageMapping("/send/key/{roomId}")
    @SendTo("/room/send/key/{roomId}")
    public String sendKey(@Payload String message,@DestinationVariable(value = "roomId") int roomId) {
        return message;
    }
}
