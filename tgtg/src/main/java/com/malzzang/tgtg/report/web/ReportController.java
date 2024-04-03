package com.malzzang.tgtg.report.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.report.dto.ReportDTO;
import com.malzzang.tgtg.report.service.ReportService;

@Controller
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	@PostMapping("insertReport")
	@ResponseBody
	public boolean insertReport(@RequestBody ReportDTO report) {
		
		return reportService.insertReport(report);
	}
}
