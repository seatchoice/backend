package com.example.seatchoice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class MainController {

	@GetMapping("/")
	public ResponseEntity<Void> index() {
		return ResponseEntity.ok().build();
	}

}
