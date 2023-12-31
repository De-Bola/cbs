package com.tuum.cbs;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication(scanBasePackages = {"com.tuum.cbs.*"})
public class CbsTuumTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CbsTuumTaskApplication.class, args);
	}

	//uri -> http://localhost:8080/swagger-ui.html

}
