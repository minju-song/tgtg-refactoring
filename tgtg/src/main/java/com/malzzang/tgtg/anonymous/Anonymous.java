package com.malzzang.tgtg.anonymous;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
public class Anonymous {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "anonymous_id")
	private int anonymousId;
	
	@Column(name = "anonymous_nickname")
	private String anonymousNickname;
	
	@Column(name = "anonymous_image")
	private String anonymousImage;
	
	@Column(name = "anonymous_image_name")
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
