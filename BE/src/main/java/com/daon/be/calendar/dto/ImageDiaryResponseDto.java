package com.daon.be.calendar.dto;

import com.daon.be.global.infra.S3Uploader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDiaryResponseDto {

    private Long id;
    private Long childId;
    private String imageUrl;
    private String diaryText;
    private LocalDateTime createdAt;

    public static ImageDiaryResponseDto from(com.daon.be.calendar.entity.ImageDiary entity) {
        return new ImageDiaryResponseDto(
            entity.getId(),
            entity.getCalendar().getChild().getId(),
            entity.getImageUrl(),
            entity.getDiaryText(),
            entity.getCreatedAt()
        );
    }
    
    // S3Uploader를 사용하여 프리사인 URL 생성
    public static ImageDiaryResponseDto from(com.daon.be.calendar.entity.ImageDiary entity, S3Uploader s3Uploader) {
        String imageUrl = null;
        if (entity.getImageUrl() != null && !entity.getImageUrl().isBlank()) {
            imageUrl = s3Uploader.presignGetUrl(entity.getImageUrl());
        }
        
        return new ImageDiaryResponseDto(
            entity.getId(),
            entity.getCalendar().getChild().getId(),
            imageUrl,
            entity.getDiaryText(),
            entity.getCreatedAt()
        );
    }
}
