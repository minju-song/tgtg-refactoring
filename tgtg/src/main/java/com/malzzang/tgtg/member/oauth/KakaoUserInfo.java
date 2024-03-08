package com.malzzang.tgtg.member.oauth;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes; //oauth2User.getAttributes()
	
	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProvideId() {
		return "kakao"+"_"+String.valueOf(attributes.get("id"));
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getMemberEmail() {
		Map<String, Object> kakaoAccount = (Map)attributes.get("kakao_account");
		return (String) kakaoAccount.get("email");
	}
}
