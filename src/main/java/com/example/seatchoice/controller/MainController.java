package com.example.seatchoice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class MainController {

	@GetMapping("/")
	public ResponseEntity<Void> main() {
		return ResponseEntity.ok().build();
	}

}
