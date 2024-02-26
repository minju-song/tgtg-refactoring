package com.malzzang.tgtg.member.oauth;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo{
	
	private Map<String, Object> attributes; //oauth2User.getAttributes()
	
	public GoogleUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProvideId() {
		return "google"+"_"+(String) attributes.get("sub");
	}

	@Override
	public String getProvider() {
		return "google";
	}

	@Override
	public String getMemberEmail() {
		return (String) attributes.get("email");
	}
	
}
