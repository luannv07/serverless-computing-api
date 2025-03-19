package com.luannv.controller;

import com.luannv.service.ImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
public class ImageController {
	ImageService imageService;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("img") MultipartFile multipartFile) {
		boolean status = imageService.uploadToS3(multipartFile);

		Map<String, String> response = new HashMap<>();
		response.put("status", String.valueOf(status));
		response.put("timestamp", String.valueOf(System.currentTimeMillis()));

		return ResponseEntity.ok().body(response);
	}
}
