package com.daon.be.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDiaryListResponseDto {

    private List<ImageDiaryResponseDto> diaries;

}