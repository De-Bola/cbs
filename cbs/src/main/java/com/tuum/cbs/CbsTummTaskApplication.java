package com.tuum.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tuum.cbs.common.messaging"})
public class CbsTummTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CbsTummTaskApplication.class, args);
	}

}
