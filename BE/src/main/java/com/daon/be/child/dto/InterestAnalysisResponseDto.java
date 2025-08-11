package com.daon.be.child.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterestAnalysisResponseDto {
    private List<String> keywords;
    private String report;
    private String summary; // 대화 요약
}