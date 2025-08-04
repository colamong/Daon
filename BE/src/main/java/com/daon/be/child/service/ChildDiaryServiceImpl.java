package com.daon.be.child.service;

import com.daon.be.ai.service.ChildDiaryImageGptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.child.infra.ChildDiaryS3Uploader;
import com.daon.be.ai.service.ChildDiaryImageGptService;
import com.daon.be.conversation.entity.ConversationResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildDiaryServiceImpl implements ChildDiaryService {

    private final ConversationResultRepository conversationResultRepository;
    private final ChildDiaryImageGptService imageGenerationService;
    private final ChildDiaryS3Uploader diaryS3Uploader;

    @Transactional
    @Override
    public String createAndUploadDiary(Long conversationResultId) {

        // 대화 결과(요약) 엔티티 조회
        ConversationResult result = conversationResultRepository.findById(conversationResultId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대화(요약)가 존재하지 않습니다."));

        // summary(그림일기 소재) 추출
        String summary = result.getAnalysisResult();

        // AI 도메인으로 그림일기 이미지(byte[]) 생성
        byte[] imageBytes = imageGenerationService.generateDiaryImage(summary);

        // S3 업로드 - PK 기준으로 경로를 구분할 수도 있음
        String key = "diary/%d/%s.png".formatted(conversationResultId, java.util.UUID.randomUUID());
        String s3Url = diaryS3Uploader.upload(key, imageBytes, "image/png");

        log.info("그림일기 생성 및 S3 업로드 완료: {}", s3Url);
        return s3Url;
    }
}
