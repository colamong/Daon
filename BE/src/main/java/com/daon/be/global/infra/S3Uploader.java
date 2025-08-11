package com.daon.be.global.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

	private final S3Client s3Client;
	private final S3Presigner presigner;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// 기본 프리사인 만료시간 초 단위 미설정이면 3600초
	@Value("${app.s3.presign.expire-seconds:3600}")
	private long defaultExpireSeconds;

	// 업로드 후 key 반환 프라이빗 업로드
	public String upload(String key, byte[] bytes, String contentType) {
		PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(contentType)
				.build();
		s3Client.putObject(putReq, RequestBody.fromBytes(bytes));
		log.info("S3 업로드 성공 {}/{}", bucket, key);
		return key;
	}

	// 편의 오버로드 기본 만료시간 사용
	public String presignGetUrl(String key) {
		return presignGetUrl(key, defaultExpireSeconds);
	}

	// 프라이빗 객체 조회용 프리사인 URL 생성
	public String presignGetUrl(String key, long expireSeconds) {
		if (key == null || key.isBlank()) return null;
		GetObjectRequest get = GetObjectRequest.builder()
				.bucket(bucket).key(key).build();
		GetObjectPresignRequest req = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofSeconds(expireSeconds))
				.getObjectRequest(get).build();
		URL url = presigner.presignGetObject(req).url();
		return url.toString();
	}
}
