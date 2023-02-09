package com.example.seatchoice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SeatchoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatchoiceApplication.class, args);
	}

}
