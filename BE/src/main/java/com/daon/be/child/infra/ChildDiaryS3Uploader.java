package com.daon.be.child.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChildDiaryS3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(String key, byte[] bytes, String contentType) {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(contentType)
                .build();

        s3Client.putObject(putReq, RequestBody.fromBytes(bytes));
        log.info("S3 업로드 성공: {}/{}", bucket, key);

        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }
}