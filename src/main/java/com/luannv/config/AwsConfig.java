package com.luannv.config;

import com.luannv.entity.Image;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {
	private static final Region AWS_REGION = Region.AP_SOUTHEAST_1;
	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
						.region(AWS_REGION)
						.build();
	}
	@Bean
	public DynamoDbClient dynamoDbClient() {
		return DynamoDbClient.builder()
						.region(AWS_REGION)
						.build();
	}
	@Bean
	public DynamoDbTable<Image> imageDynamoDbTable(DynamoDbClient dynamoDbClient) {
		DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
						.dynamoDbClient(dynamoDbClient)
						.build();
		return dynamoDbEnhancedClient.table("images", TableSchema.fromBean(Image.class));
	}
}
