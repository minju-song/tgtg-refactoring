package com.malzzang.tgtg.forbid;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Forbid {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="forbid_id")
	private int forbidId;
	
	@Column(name="forbid_word")
	private String forbidWord;

}
