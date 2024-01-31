package com.malzzang.tgtg.subject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Subject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "subject_id")
	private int subjectId;
	
	@Column(name = "subject_title")
	private String subjectTitle;
	
	@Column(name = "subject_answer_a")
	private String subjectAnswerA;
	
	@Column(name = "subject_answer_b")
	private String subjectAnswerB;
}
