package com.daon.be.child.dto;

import com.daon.be.conversation.entity.ConversationResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FullAnalysisResponseDto {
    private String emotionReport;
    private String summary;
    private List<String> keywords;
    private String interestReport;
    private LocalDateTime createdAt;

    public static FullAnalysisResponseDto from(ConversationResult result, List<String> keywords) {
        return new FullAnalysisResponseDto(
            result.getEmotionReport(),
            result.getAnalysisResult(),
            keywords,
            result.getInterestReport(),
            result.getCreatedAt()
        );
    }
}