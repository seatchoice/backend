package com.example.seatchoice.controller;

import com.example.seatchoice.dto.response.ImageResponse;
import com.example.seatchoice.service.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
	private final ImageService imageService;

	@GetMapping
	public ResponseEntity<List<ImageResponse>> getImages(@RequestParam Long seatId) {
		return ResponseEntity.ok(imageService.getImages(seatId));
	}
}
