package com.daon.be.ai.service;

import org.springframework.stereotype.Component;

@Component
public class DiaryImagePromptBuilder {
    public String build(String summary) {
        
        // 스타일, 화풍, 톤 등 모두 여기에
        return "아동용 동화책 삽화 스타일, 색연필+크레용 질감, 두꺼운 손그림 외곽선, 부드러운 명암, 따뜻한 파스텔 톤, 종이 질감이 보이는 스케치 스트로크, 단순화된 귀여운 비사실적 카툰 스타일로 그림 그려줘. 사람들은 동양인으로 설정하고, 생성되는 사진에 텍스트는 넣지마 " + summary;
    }
}
