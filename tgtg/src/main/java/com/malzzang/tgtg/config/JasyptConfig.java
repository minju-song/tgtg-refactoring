package com.malzzang.tgtg.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class JasyptConfig {
//	@Value("${jasypt_encryptor_password}")
//	   private String password;
//
//	@Bean("jasyptStringEncryptor")
//	    public StringEncryptor stringEncryptor() {
//	        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//	        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//	        config.setPassword(password);
//	        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
//	        config.setKeyObtentionIterations("1000");
//	        config.setPoolSize("1");
//	        config.setProviderName("SunJCE");
//	        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//	        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
//	        config.setStringOutputType("base64");
//	        encryptor.setConfig(config);
//	        return encryptor;
//	    }
}
