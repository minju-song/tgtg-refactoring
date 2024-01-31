package com.malzzang.tgtg.anonymous;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
}
