// src/services/learningService.js
import apiClient from '@/utils/axios.js'
import { useAuthStore } from '@/store/auth'
import { themeImageMap, themeDefaultImage } from '@/utils/themeImages'

/**
 * 항상 최신 로그인 유저 ID 가져오기 (필요 시 사용)
 */
function getUserId() {
  const auth = useAuthStore()
  return auth.user?.id || null
}

/* =========================================================
 * DTO → ViewModel 매핑
 * ---------------------------------------------------------
 * 백엔드 DTO 스키마 (참고)
 * - ThemeResponseDTO   : { id, name, description }
 * - ChapterResponseDTO : { id, title, description }
 * - QuestionResponseDTO: { id, questionText, audioUrl, choices }
 * - ChoiceResponseDTO  : { id, choiceText, audioUrl, isCorrect }
 * =======================================================*/

/** ThemeResponseDTO => ViewModel */
export function mapTheme(dto) {
  const id = Number(dto.id) // 문자열 가능성 대비
  return {
    id,
    title: dto.name,
    description: dto.description,
    image: themeImageMap[id] ?? themeDefaultImage, // 프론트 고정 이미지 매핑
  }
}


/** ChapterResponseDTO => ViewModel */
export function mapChapter(dto) {
  return {
    id: dto.id,
    title: dto.title,
    description: dto.description,
  }
}

/** ChoiceResponseDTO => ViewModel */

const normalizeBool = (v) => v === true || v === 'true' || v === 1 || v === '1'

export function mapChoice(dto) {
  const raw = dto.isCorrect ?? dto.correct ?? false  
  return {
    id: dto.id,
    text: dto.choiceText,
    audioUrl: dto.audioUrl,
    isCorrect: normalizeBool(raw),
    pronunciation: '', // 서버에 발음 텍스트가 없으므로 공란 유지
  }
}

/** QuestionResponseDTO => ViewModel */
export function mapQuestion(dto) {
  return {
    id: dto.id,
    question: dto.questionText,
    questionPronunciation: '', // 서버 미제공
    audioUrl: dto.audioUrl,
    answers: (dto.choices ?? []).map(mapChoice),
  }
}

/* =========================================================
 * API 호출부 (매핑된 ViewModel 반환)
 * =======================================================*/

/**
 * 테마 목록 조회 (매핑된 ViewModel 배열 반환)
 * GET /api/study/themes
 */
export async function getThemes() {
  const res = await apiClient.get('/api/study/themes')
  return (res.data ?? []).map(mapTheme)
}

/**
 * 특정 테마의 챕터 목록 조회 (매핑된 ViewModel 배열 반환)
 * GET /api/study/themes/{themeId}/chapters
 */
export async function getChaptersByTheme(themeId) {
  if (!themeId) throw new Error('themeId가 필요합니다.')
  const res = await apiClient.get(`/api/study/themes/${themeId}/chapters`)
  return (res.data ?? []).map(mapChapter)
}

/**
 * 특정 챕터의 질문 목록 조회 (매핑된 ViewModel 배열 반환)
 * GET /api/study/chapters/{chapterId}/questions
 */
export async function getQuestionsByChapter(chapterId) {
  if (!chapterId) throw new Error('chapterId가 필요합니다.')
  const res = await apiClient.get(`/api/study/chapters/${chapterId}/questions`)
  return (res.data ?? []).map(mapQuestion)
}

/**
 * 정답 보기 텍스트 조회 (서버 판정이 필요할 때 사용)
 * GET /api/study/choice/correct/{questionId}
 */
export async function getCorrectChoiceText(questionId) {
  if (!questionId) throw new Error('questionId가 필요합니다.')
  const res = await apiClient.get(`/api/study/choice/correct/${questionId}`)
  return res.data // string
}

/* =========================================================
 * (선택) 서비스 객체 기본 export
 * - import learningService from '@/services/learningService'
 * =======================================================*/
const learningService = {
  getThemes,
  getChaptersByTheme,
  getQuestionsByChapter,
  getCorrectChoiceText,
  // 매퍼도 필요하면 외부에서 사용 가능
  mapTheme,
  mapChapter,
  mapQuestion,
  mapChoice,
}

export default learningService
