package com.malzzang.tgtg.anonymous;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "anonymous")
public class Anonymous {
	
	@Id
	private String id;
	
	@Field("anonymous_id")
	private int anonymousId;
	
	@Field("anonymous_nickname")
	private String anonymousNickname;
	
	@Field("anonymous_image")
	private String anonymousImage;
	
	@Field("anonymous_image_name")
	private String anonymousImageName;
	
	public AnonymousDTO toResponseDto(){
        return AnonymousDTO.builder()
                .anonymousId(anonymousId)
                .anonymousNickname(anonymousNickname)
                .anonymousImage(anonymousImage)
                .anonymousImageName(anonymousImageName)
                .build();
	}

}
