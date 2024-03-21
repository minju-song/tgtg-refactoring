package com.malzzang.tgtg.subject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubjectDTO {
	
	int subjectId;
	private String subjectTitle;
	private String subjectAnswerA;
	private String subjectAnswerB;
}
