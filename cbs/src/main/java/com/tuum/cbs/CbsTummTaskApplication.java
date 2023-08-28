package com.tuum.cbs;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication(scanBasePackages = {"com.tuum.cbs.*"})
public class CbsTummTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CbsTummTaskApplication.class, args);
	}

}
