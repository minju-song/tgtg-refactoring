package com.malzzang.tgtg.anonymous.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;

@Controller
public class AnonymousController {
	
	@Autowired
	AnonymousService anonymousService;
	

	
}
