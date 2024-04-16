package com.malzzang.tgtg.proxyserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ProxyController {

    public Map<String, Object> getChatData(String memberId, String type){

        Map<String, Object> anonymous = new HashMap<>();
        Map<String, Object> room = new HashMap<>();

        // 2서버의 startChat 함수 호출을 위한 요청 데이터 준비
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("memberId", memberId);
        requestData.put("type",type);

        RestTemplate restTemplate = new RestTemplate();

        // 2서버로 요청 전송 및 응답 받기
//        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://localhost:8099/getChatroom", requestData, String.class);

        // 2서버로부터 받은 데이터를 바탕으로 타임리프 뷰 리턴
        String strData = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 Map으로 변환
            Map<String, Object> responseMap = objectMapper.readValue(strData, new TypeReference<Map<String, Object>>() {});

            // Map에서 필요한 데이터 추출
            anonymous = (Map<String, Object>) responseMap.get("anonymousDTO");
            room = (Map<String, Object>) responseMap.get("chatroom");


        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("anonymous", anonymous);
        result.put("room", room);

        return result;
    }

    public Map<String, Object> getGame(int roomId) {

        // 2서버의 startChat 함수 호출을 위한 요청 데이터 준비
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("roomId", roomId);

        // 2서버로 요청 전송 및 응답 받기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://localhost:8099/getGame", requestData, String.class);

        String strData = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 문자열을 Map으로 변환
        Map<String, Object> responseMap = null;
        try {
            responseMap = objectMapper.readValue(strData, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Map에서 필요한 데이터 추출
        Map<String, Object> room = new HashMap<>();
        room = (Map<String, Object>) responseMap.get("room");

        return room;
    }
}
