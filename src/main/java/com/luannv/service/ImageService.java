package com.luannv.service;

import com.luannv.entity.Image;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImageService {
	S3Client s3Client;
	DynamoDbTable<Image> imageDynamoDbTable;
	static String BUCKET_NAME = "aws-bucket-luannv";

	public boolean uploadToS3(MultipartFile multipartFile) {
		if (multipartFile == null || multipartFile.isEmpty())
			return false;
		try {
			s3Client.putObject(PutObjectRequest.builder()
											.contentLength(multipartFile.getSize())
											.bucket(BUCKET_NAME)
											.contentType(multipartFile.getContentType())
											.key(multipartFile.getOriginalFilename())
							.build(), RequestBody.fromBytes(multipartFile.getBytes()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
