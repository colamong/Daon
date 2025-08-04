package com.daon.be.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDiaryResponseDto {

    private Long id;
    private String imageUrl;
    private String diaryText;
    private LocalDateTime createdAt;

    public static ImageDiaryResponseDto from(com.daon.be.calendar.entity.ImageDiary entity) {
        return new ImageDiaryResponseDto(
                entity.getId(),
                entity.getImageUrl(),
                entity.getDiaryText(),
                entity.getCreatedAt()
        );
    }
}
