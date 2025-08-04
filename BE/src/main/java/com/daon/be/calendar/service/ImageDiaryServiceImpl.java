package com.daon.be.calendar.service;

import com.daon.be.ai.service.ChildDiaryImageGptService;
import com.daon.be.calendar.dto.ImageDiaryResponseDto;
import com.daon.be.calendar.entity.Calendar;
import com.daon.be.calendar.entity.ImageDiary;
import com.daon.be.calendar.repository.ImageDiaryRepository;
import com.daon.be.global.infra.S3Uploader;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.entity.ConversationResult;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageDiaryServiceImpl implements ImageDiaryService {

    private final ImageDiaryRepository imageDiaryRepository;
    private final ChildDiaryImageGptService childDiaryImageGptService;
    private final ConversationResultRepository conversationResultRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    @Override
    public Long createDiary(Long conversationResultId) {

        // 대화 결과 조회
        ConversationResult result = conversationResultRepository.findById(conversationResultId)
            .orElseThrow(() -> new IllegalArgumentException("해당 대화 요약이 존재하지 않습니다."));

        // summary 추출
        String summary = result.getAnalysisResult();

        // 이미지 생성
        byte[] imageBytes = childDiaryImageGptService.generateDiaryImage(summary);

        // S3 업로드
        String key = "diary/" + conversationResultId + "/" + UUID.randomUUID() + ".png";
        String s3Url = s3Uploader.upload(key, imageBytes, "image/png");

        // calendar 조회
        Calendar calendar = result.getCalendar();

        // diary 텍스트는 summary 그대로 사용
        String diaryText = summary;

        // ImageDiary 생성 및 저장
        ImageDiary diary = ImageDiary.create(calendar, result, s3Url, diaryText);
        imageDiaryRepository.save(diary);

        return diary.getId();
    }


    @Override
    public ImageDiaryResponseDto getDiary(Long diaryId) {
        ImageDiary diary = imageDiaryRepository.findById(diaryId)
            .orElseThrow(() -> new EntityNotFoundException("해당 그림일기를 찾을 수 없습니다."));
        return ImageDiaryResponseDto.from(diary); // Dto 변환 메소드 필요
    }

    @Override
    public List<ImageDiaryResponseDto> getMonthlyDiaries(Long childId, int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).plusDays(1);
        return imageDiaryRepository
            .findAllByCalendar_Child_IdAndCreatedAtBetweenOrderByCreatedAtDesc(childId, start, end)
            .stream()
            .map(ImageDiaryResponseDto::from)
            .toList();
    }
}
