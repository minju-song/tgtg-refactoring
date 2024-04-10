package com.malzzang.tgtg.report.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.member.Member;
import com.malzzang.tgtg.member.MemberRepository;
import com.malzzang.tgtg.member.dto.MemberDTO;
import com.malzzang.tgtg.member.service.MemberService;
import com.malzzang.tgtg.report.Report;
import com.malzzang.tgtg.report.ReportRepository;
import com.malzzang.tgtg.report.dto.ReportDTO;

@Service
public class ReportServiceImpl implements ReportService {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	ReportRepository reportRepository;
	
	@Autowired
	AnonymousService anonymousService;

	@Override
	public boolean insertReport(ReportDTO reportDTO) {
		
		Member reporter = memberRepository.findByMemberId(anonymousService.findMemberId(reportDTO.getRoomId(), reportDTO.getReporterId()));
		Member reported = memberRepository.findByMemberId(anonymousService.findMemberId(reportDTO.getRoomId(),reportDTO.getReportedId()));
		
		Report report = new Report();
		report.setReportCategory(reportDTO.getReportCategory());
		if (reportDTO.getReportChat() != null) {
            report.setReportChat(reportDTO.getReportChat());
        }
		// 대한민국 시간대 설정 및 현재 시간 가져오기
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // ZonedDateTime을 Date로 변환
        Date now = Date.from(zdt.toInstant());
        
        report.setReportDate(now);
		report.setReportedId(reported);
		report.setReporterId(reporter);
		report.setReportReason(reportDTO.getReportReason());
		report.setReportStatus("접수");
		
		try {
	        reportRepository.save(report);
	        return true; 
	    } catch (Exception e) {
	        System.err.println("Error saving report: " + e.getMessage());
	        return false; 
	    }
	}

}
