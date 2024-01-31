package com.malzzang.tgtg.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.malzzang.tgtg.test2.test2;
import com.malzzang.tgtg.test2.test2Repository;

@RestController
public class testController {
	
	private final testRepository repository;
	private final test2Repository repository2;
	
	public testController(testRepository r1, test2Repository r2) {
        this.repository = r1;
        this.repository2 = r2;
    }
	
	@GetMapping("/test")
    public void addTest() {
		test t = new test();

		t.setName("수민");
		t.setEmail("bbb");
		repository.save(t);
		
		List<test> result = new ArrayList<>();
		result = repository.findAll();
		for(int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
		
		test2 t2 = new test2();

		t2.setId(5);
		t2.setT(t);
		t2.setSchool("아무고");
		repository2.save(t2);
		
		test2 t3 = new test2();
		t3.setT(t);
		t3.setSchool("아무대");
		repository2.save(t3);
		
		List<test2> result2 = new ArrayList<>();
		result2 = repository2.findAll();
		for(int i = 0; i < result2.size(); i++) {
			System.out.println(result2.get(i));
		}
    }
}
