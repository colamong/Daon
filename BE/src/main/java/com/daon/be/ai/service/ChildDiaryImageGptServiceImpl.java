package com.daon.be.ai.service;

import com.daon.be.ai.client.ImageDownloader;
import com.daon.be.ai.client.OpenAiImageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildDiaryImageGptServiceImpl implements ChildDiaryImageGptService {

    private final DiaryImagePromptBuilder promptBuilder;      // 프롬프트 빌더 (스타일 포함)
    private final OpenAiImageClient openAiImageClient;        // GMS DALL·E 3 호출
    private final ImageDownloader imageDownloader;            // 이미지 다운로드

    @Override
    public byte[] generateDiaryImage(String summary) {

        String prompt = promptBuilder.build(summary);                   // 프롬프트 생성 (스타일 포함)

        String imageUrl = openAiImageClient.generateImage(prompt);      // AI로 이미지 생성, URL 반환

        return imageDownloader.download(imageUrl);                      // URL로 이미지 다운로드 (byte[])
    }
}