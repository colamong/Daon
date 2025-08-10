package com.daon.be.ai.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GptFullAnalysisResponseDto {
    private String emotion;
    private String summary;
    private List<String> keywords;
    private String report;
}