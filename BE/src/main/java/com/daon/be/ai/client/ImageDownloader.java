package com.daon.be.ai.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ImageDownloader {

    public byte[] download(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("이미지 다운로드 실패", e);
        }
    }
}