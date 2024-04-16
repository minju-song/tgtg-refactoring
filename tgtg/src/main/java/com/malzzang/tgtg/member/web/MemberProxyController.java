package com.malzzang.tgtg.member.web;

import com.malzzang.tgtg.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MemberProxyController {

    @Autowired
    MemberService memberService;

    @CrossOrigin(origins = "*")
    @PostMapping("/saveResult")
    public void saveResult(@RequestBody Map<String, Object> requestData){
        String memberId = (String) requestData.get("memberId");
        String result = (String) requestData.get("result");

        if(result.equals("win")) {
            memberService.increaseWin(memberId);
        }
        else if(result.equals("lose")) {
            memberService.increaseLose(memberId);
        }
        else memberService.increaseDraw(memberId);
    }
}
