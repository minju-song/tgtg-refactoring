package com.malzzang.tgtg.socket.game;

import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.chatroom.service.ChatroomService;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.chatroom.service.ReadyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;

@Controller
public class GameController {

    @Autowired
    ConnectedUserService connectedUserService;

    @Autowired
    ChatroomService chatroomService;

    @Autowired
    AnonymousService anonymousService;

    @Autowired
    ReadyUserService readyUserService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //게임방 타이머 현재시간
    @MessageMapping("/{roomId}/sendTime")
    public void gameTimer(@DestinationVariable int roomId) {
        System.out.println("입장완");
        LocalTime endTime = connectedUserService.gameStartUser(roomId);
        System.out.println(endTime+">>");
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


//    게임 결과 전송
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
            anonymousService.deleteCount(roomId);
//    		결과전송
            simpMessagingTemplate.convertAndSend("/room/" + roomId + "/getResult", result);
        }
    }
}
