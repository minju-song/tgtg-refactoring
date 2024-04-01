package com.malzzang.tgtg.mail;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface MailService {
	 // 메일 내용 작성
    MimeMessage creatMessage(String to, String email, String name, String content) throws MessagingException, UnsupportedEncodingException;

    // 메일 발송
    Boolean sendSimpleMessage(String to, String email, String name, String content) throws Exception;
}
