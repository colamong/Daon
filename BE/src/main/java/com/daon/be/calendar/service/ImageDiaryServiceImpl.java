package com.daon.be.calendar.service;

import com.daon.be.ai.service.ChildDiaryImageGptService;
import com.daon.be.ai.service.ChildDiaryImageGptServiceImpl;
import com.daon.be.calendar.dto.ImageDiaryResponseDto;
import com.daon.be.calendar.entity.Calendar;
import com.daon.be.calendar.entity.ImageDiary;
import com.daon.be.calendar.repository.CalendarRepository;
import com.daon.be.calendar.repository.ImageDiaryRepository;
import com.daon.be.child.infra.ChildDiaryS3Uploader;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.entity.ConversationResult;
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
    private final ChildDiaryS3Uploader s3Uploader;

    @Transactional
    @Override
    public Long createDiary(Long conversationResultId) {

        // 1. 대화 결과(요약) 엔티티 조회
        ConversationResult result = conversationResultRepository.findById(conversationResultId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대화 요약이 존재하지 않습니다."));

        // 2. summary(그림일기 소재) 추출
        String summary = result.getAnalysisResult();

        // 4. AI 도메인으로 그림일기 이미지(byte[]) 생성
        byte[] imageBytes = childDiaryImageGptService.generateDiaryImage(summary);

        // 5. S3 업로드
        String key = "diary/" + conversationResultId + "/" + UUID.randomUUID() + ".png";
        String s3Url = s3Uploader.upload(key, imageBytes, "image/png");

        // 6. 그림일기 저장 (팩토리 메소드 활용, calendar → result로)
        ImageDiary diary = ImageDiary.create(result, s3Url, summary);
        imageDiaryRepository.save(diary);

        return diary.getId();
    }

    @Override
    public ImageDiaryResponseDto getDiary(Long diaryId) {
        return null;
    }

    @Override
    public List<ImageDiaryResponseDto> getMonthlyDiaries(Long childId, int year, int month) {
        return null;
    }
}
