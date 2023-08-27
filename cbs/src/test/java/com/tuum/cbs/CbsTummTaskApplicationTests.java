package com.tuum.cbs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@ComponentScan(basePackages = {"com.tuum.cbs"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CbsTummTaskApplicationTests {

	@Test
	void contextLoads() {
	}

	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost";
	private static RestTemplate restTemplate;
	private

	@BeforeAll
	static void init(){
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setup(){
		baseUrl = baseUrl + ":" + port + "/api";
	}

}
