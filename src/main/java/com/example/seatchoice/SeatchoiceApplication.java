package com.example.seatchoice;

import com.example.seatchoice.config.AppProperties;
import com.example.seatchoice.config.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties({CorsProperties.class, AppProperties.class})
public class SeatchoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatchoiceApplication.class, args);
	}

}
