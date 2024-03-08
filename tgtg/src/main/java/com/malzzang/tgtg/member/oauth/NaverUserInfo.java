package com.malzzang.tgtg.member.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes; //oauth2User.getAttributes()
	
	public NaverUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProvideId() {
		return "naver"+"_"+(String) attributes.get("id");
	}

	@Override
	public String getProvider() {
		return "naver";
	}

	@Override
	public String getMemberEmail() {
		return (String) attributes.get("email");
	}

}
