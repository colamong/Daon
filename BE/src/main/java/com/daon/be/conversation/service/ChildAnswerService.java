package com.daon.be.conversation.service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.daon.be.child.entity.ChildProfile;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.dto.GptAudioResponseDto;
import com.daon.be.conversation.entity.ConversationTopic;

import lombok.RequiredArgsConstructor;


public interface ChildAnswerService {
	/**
	 * 테스트 등 단순 답변 저장용.
	 */
	void saveAnswer(ChildAnswerRequestDto dto, ChildProfile child, ConversationTopic topic);

	/**
	 * 신규 대화 토픽 생성
	 */
	ConversationTopic createTopic(ConversationTopicRequestDto dto);

	/**
	 * 사용자가 답변한 내용을 Redis에 저장하고 다음 질문을 생성 및 반환.
	 * 마지막 스텝이면 Redis -> DB flush도 수행.
	 */
	GptAudioResponseDto saveAnswerAndGetNextQuestionAudio(ChildAnswerRequestDto dto);

	/**
	 * Redis에 저장된 답변 리스트 조회 (원시 Object 리스트 반환)
	 */
	List<Object> getAnswersInRedis(String redisKey);

	/**
	 * Redis에 쌓인 답변들을 DB에 저장하고, 저장된 ConversationResult ID를 반환합니다.
	 * 저장 실패 또는 데이터 없으면 음수 반환 가능.
	 */
	Long flushAnswersFromRedis(Long childId, Long topicId);

	/**
	 * 다음에 사용해야 할 Topic ID 조회
	 */
	Long getNextTopicId(Long childId);

	/**
	 * 다음에 사용해야 할 Topic 엔티티 조회
	 */
	ConversationTopic getNextTopic(Long childId);


	/**
	 * 다음 질문을 생성하는 프롬프트 로직 필요 시 서비스 레벨에서도 공개할 수 있음
	 */
	// String generateNextPrompt(ChildAnswerRequestDto dto);  // 선택적 공개

}
