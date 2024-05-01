package com.malzzang.tgtg.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malzzang.tgtg.member.service.MemberService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SaveResultResponse {

    @Autowired
    MemberService memberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "SaveResultRequest", groupId = "group1")
    public void listener(ConsumerRecord<String, String> record) {
        String jsonValue = record.value();
        System.out.println(">>>"+jsonValue);
        Map<String, Object> dataMap = null;
        try {
            dataMap = objectMapper.readValue(jsonValue, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String result = (String) dataMap.get("result");
        String memberId = (String) dataMap.get("memberId");

        if(result.equals("win")) {
            memberService.increaseWin(memberId);
        }
        else if(result.equals("lose")) {
            memberService.increaseLose(memberId);
        }
        else memberService.increaseDraw(memberId);

    }
}
