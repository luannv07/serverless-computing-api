package com.luannv.service;

import com.luannv.dto.request.AuthorRequest;
import com.luannv.entity.Image;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImageService {
	S3Client s3Client;
	DynamoDbTable<Image> imageDynamoDbTable;
	static String BUCKET_NAME = "aws-bucket-luannv";
	Integer limit = 5;

	public Long uploadToS3(MultipartFile multipartFile) {
		byte[] img = null;
		try {
			img = multipartFile.getBytes();
		} catch (IOException e) {
			return 0L;
		}
		String contentType = multipartFile.getContentType();
		if (multipartFile == null || multipartFile.isEmpty() || contentType == null || !contentType.startsWith("image"))
			return 0L;
		Long size = multipartFile.getSize();
		String uuid = UUID.randomUUID().toString();
		String fileName = uuid + "_" + multipartFile.getOriginalFilename();
		s3Client.putObject(PutObjectRequest.builder()
						.contentLength(size)
						.bucket(BUCKET_NAME)
						.contentType(contentType)
						.key(fileName)
						.build(), RequestBody.fromBytes(img));

		String url = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(BUCKET_NAME).key(fileName).build()).toString();
		Instant times = Instant.now();

		addToDatabase(uuid, fileName, url, size, times);
		return 1L;
	}

	public void addToDatabase(String uuid, String fileName, String url, Long size, Instant times) {
		imageDynamoDbTable.putItem(Image.builder()
						.id(uuid)
						.name(fileName)
						.url(url)
						.size(size)
						.time(LocalDateTime.ofInstant(times, ZoneId.of("Asia/Ho_Chi_Minh")))
						.author("Anonymous")
						.build());
	}

	public Image getImageById(String id) {
		return imageDynamoDbTable.getItem(Key.builder()
						.partitionValue(id)
						.build());
	}

	public void deleteItemById(String id) {
		Image image = getImageById(id);
		imageDynamoDbTable.deleteItem(Key.builder()
						.partitionValue(id)
						.build());
		s3Client.deleteObject(DeleteObjectRequest.builder()
						.bucket(BUCKET_NAME)
						.key(image.getName())
						.build());
	}

	public List<Image> getAllItems() {
		return imageDynamoDbTable.scan().items().stream().toList();
	}

	public Image editById(String id, AuthorRequest request) {
		Image image = getImageById(id);
		image.setAuthor(request.getName());
		imageDynamoDbTable.updateItem(image);
		return image;
	}
}
