package com.daon.be.calendar.service;

import com.daon.be.calendar.dto.ImageDiaryResponseDto;

import java.util.List;

public interface ImageDiaryService {

    Long createDiary(Long conversationResultId); // 그림일기를 생성하고 저장된 diary의 PK 반환

    ImageDiaryResponseDto getDiary(Long diaryId); // 그림일기 단건 조회

    List<ImageDiaryResponseDto> getMonthlyDiaries(Long childId, int year, int month); // 특정 아이의 월별 그림일기 목록 조회
}
