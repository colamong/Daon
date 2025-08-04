package com.daon.be.ai.service;

import org.springframework.stereotype.Component;

@Component
public class DiaryImagePromptBuilder {
    public String build(String summary) {
        
        // 스타일, 화풍, 톤 등 모두 여기에
        return "아이가 그린 것처럼 크레파스를 사용해서 그린 그림체로, 따뜻하고 귀엽게 그려줘. " + summary;
    }
}
