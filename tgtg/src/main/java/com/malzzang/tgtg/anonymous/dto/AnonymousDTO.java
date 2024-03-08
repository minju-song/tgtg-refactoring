package com.malzzang.tgtg.anonymous.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnonymousDTO {
	private int anonymousId;
	private String anonymousImage;
	private String anonymousNickname;
	private String anonymousImageName;
}
