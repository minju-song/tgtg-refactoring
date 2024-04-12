package com.malzzang.tgtg.subject.domain;

import com.malzzang.tgtg.subject.dto.SubjectDTO;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "subject")
public class Subject {
	
	@Id
	private String _id;
	
	@Field("subject_title")
	private String subjectTitle;
	
	@Field("subject_answer_a")
	private String subjectAnswerA;
	
	@Field("subject_answer_b")
	private String subjectAnswerB;
	
	public SubjectDTO toResponseDto() {
		return SubjectDTO.builder()
				._id(_id)
				.subjectTitle(subjectTitle)
				.subjectAnswerA(subjectAnswerA)
				.subjectAnswerB(subjectAnswerB)
				.build();
	}
}
