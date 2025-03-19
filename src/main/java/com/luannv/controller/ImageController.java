package com.luannv.controller;

import com.luannv.dto.request.AuthorRequest;
import com.luannv.dto.response.ApiResponse;
import com.luannv.entity.Image;
import com.luannv.service.ImageService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/images")
public class ImageController {
	ImageService imageService;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("img") MultipartFile multipartFile) {
		Long status = imageService.uploadToS3(multipartFile);

		Map<String, Long> response = new HashMap<>();
		response.put("status", status);
		response.put("timestamp", System.currentTimeMillis());
		response.put("luannv", 2005L);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping
	public ResponseEntity<?> getItems() {
		return ResponseEntity.ok().body(ApiResponse.<List<Image>>builder()
						.result(imageService.getAllItems())
						.code(HttpStatus.OK.value())
						.timestamp(System.currentTimeMillis())
						.message("luannv_hehehehehhhhe")
						.build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable String id) {
		imageService.deleteItemById(id);
		return ResponseEntity.ok().body(ApiResponse.<Boolean>builder()
						.code(HttpStatus.OK.value())
						.timestamp(System.currentTimeMillis())
						.result(Boolean.TRUE)
						.message("luannv_hehehehehhhhe")
						.build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editAuthorById(@PathVariable String id, @RequestBody AuthorRequest authorRequest) {
		return ResponseEntity.ok().body(ApiResponse.<Image>builder()
						.result(imageService.editById(id, authorRequest))
						.timestamp(System.currentTimeMillis())
						.code(HttpStatus.OK.value())
						.message("luannv_hehehehehhhhe")
						.build());
	}
}
