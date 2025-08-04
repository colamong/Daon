package com.daon.be.ai.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ImageDownloader {

    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] download(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }
}