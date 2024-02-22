package com.malzzang.tgtg;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TgtgApplicationTests {

	@Autowired
	   StringEncryptor jasyptStringEncryptor;
	   
	@Test
	   void encryptor() {
	      String[] datas = {
	                        "org.mariadb.jdbc.Driver"
	                        ,"jdbc:mariadb://127.0.0.1:3307/tgtgdb"
	                        ,"root"
	                        ,"maria"
	      };
	      
	      for(String data : datas) {
	         String encData = jasyptStringEncryptor.encrypt(data);
	         System.out.println(encData);
	      }
	   }

}
