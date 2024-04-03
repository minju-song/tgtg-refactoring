package com.malzzang.tgtg;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
class TgtgApplicationTests {
	
	private static final Logger logger = LoggerFactory.getLogger(TgtgApplicationTests.class);

	@Autowired
	   StringEncryptor jasyptStringEncryptor;
	   
	@Test
	   void encryptor() {
		
		String[] datas = {
	            "tgtg"
        };
	      
        for(String data : datas) {
            String encData = jasyptStringEncryptor.encrypt(data);
            System.out.println("Original: " + data + " Encrypted: " + encData);
        }
	}
	
}


