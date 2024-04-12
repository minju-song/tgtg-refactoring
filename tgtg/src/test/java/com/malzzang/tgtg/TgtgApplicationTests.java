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
				"J4IfdapxvpQJ6xFtei3InPQf2VsLMrFULP6NHKu/ytJel39bBBD16N7dFyrJsZmQIxIcdyfSyJVZtiKHjjG4eQ==",
				"K3IdHTAnZjOT23E53BZqo4uJvmjIvZD13xCF/n2oqFoMOuZZjCeCxBMW0HVElNKq",
				"98Gp6+B9daJBv7Ga/eh8gE+N0FZ/C4i7kKXfZ7zmjME+Fz2AVnWvddLf6kQr8glW/f5jKW6se11BeQaNqanktQ=="
        };
	      
        for(String data : datas) {
            String encData = jasyptStringEncryptor.encrypt(data);
            System.out.println("Original: " + data + " Encrypted: " + encData);
        }
	}
	
}


