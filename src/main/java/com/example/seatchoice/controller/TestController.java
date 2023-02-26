package com.example.seatchoice.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

	@GetMapping("/")
	public ResponseEntity<Void> test(HttpServletRequest request, HttpServletResponse response) {
		String ip = ConnectUtil.getIp(request);
		ConnectUtil.getBrowser(request);
		ConnectUtil.getOs(request);
		ConnectUtil.getWebType(request);
		log.info("======================================ip : " + ip);
		return ResponseEntity.ok().build();
	}

}
