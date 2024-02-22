package com.malzzang.tgtg.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.malzzang.tgtg.forbid.Forbid;
import com.malzzang.tgtg.forbid.ForbidRepository;
import com.malzzang.tgtg.test2.test2;
import com.malzzang.tgtg.test2.test2Repository;

@RestController
public class testController {
	
	private final testRepository repository;
	private final test2Repository repository2;
	private final ForbidRepository frepo;
	
	public testController(testRepository r1, test2Repository r2, ForbidRepository f) {
        this.repository = r1;
        this.repository2 = r2;
        this.frepo = f;
    }
	
	@GetMapping("/test")
    public void addTest() {
		Forbid forVO = new Forbid();
		forVO.setForbidWord("금지어");
		frepo.save(forVO);
    }
}
