package com.malzzang.tgtg.report;

import java.util.Date;

import com.malzzang.tgtg.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Report {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="report_id")
	private int reportId;
	
	@ManyToOne
	@JoinColumn(name="reporter_id")
	private Member reporterId;
	
	@ManyToOne
	@JoinColumn(name="reported_id")
	private Member reportedId;
	
	@Column(name="report_date")
	private Date reportDate;
	
	@Column(name="report_category")
	private String reportCategory;
	
	@Column(name="report_reason")
	private String reportReason;
	
	@Column(name="report_status")
	private String reportStatus;
	
	@Column(name="report_admin")
	private String reportAdmin;
	
	@Column(name="report_chat")
	private String reportChat;
}
