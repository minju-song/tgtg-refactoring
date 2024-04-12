package com.malzzang.tgtg.report.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDTO {
	private int reportId;
	private String reporterId;
	private String reportedId;
	private Date reportDate;
	private String reportCategory;
	private String reportReason;
	private String reportStatus;
	private String reportAdmin;
	private String reportChat;
	private int roomId;
}
