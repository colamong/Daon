package com.daon.be.ai.service;

public interface ChildDiaryImageGptService {

    // 대화 요약 내용 AI에 전달해 그림일기 이미지를 생성하고, 결과 이미지의 byte[]를 반환
    byte[] generateDiaryImage(String summary);
}
