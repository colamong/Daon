// src/services/childService.js
import axios from 'axios';

// Vite 프록시(/api → 8080) 전제. 프록시가 없다면 baseURL을 'http://localhost:8080/api' 로 바꿔주세요.
const API_BASE_URL = '/api';

// 임시 인증 토큰 (개발용)
const TEMP_AUTH_TOKEN = 'Bearer temp-token-for-development';

// ✅ 전역 axios 기본값으로 Content-Type을 고정하지 않습니다 (멀티파트 깨짐 방지)
const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  timeout: 30000,
});

// 공통 인증 헤더 (필요 시 토큰 교체)
api.defaults.headers.common['Authorization'] = TEMP_AUTH_TOKEN;

// 공통 에러 핸들러
function handleError(error, fallbackMsg) {
  if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
    throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
  }
  const msg =
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    error?.message ||
    fallbackMsg;
  throw new Error(msg);
}

export const childService = {
  // 아이 표정 기록 API
  async recordExpression(childId, conversationResultId) {
    try {
      const { data } = await api.post(`/children/${childId}/expressions/${conversationResultId}`, {});
      return data;
    } catch (error) {
      handleError(error, '표정 기록 중 오류가 발생했습니다.');
    }
  },

  // 다이어리 생성 API
  async createDiary(conversationResultId) {
    try {
      const { data } = await api.post(`/diaries/${conversationResultId}`, {});
      return data;
    } catch (error) {
      handleError(error, '다이어리 생성 중 오류가 발생했습니다.');
    }
  },

  // 대화 시작 API
  async startConversation(childId) {
    try {
      const { data } = await api.get(`/conversation/start`, { params: { childId } });
      return data;
    } catch (error) {
      handleError(error, '대화 시작 중 오류가 발생했습니다.');
    }
  },

  // 대화 답변 API
  async sendConversationAnswer(childId, topicId, step, answer, question = '') {
    try {
      const { data } = await api.post(`/conversation/answer`, {
        childId,
        topicId,
        step,
        answer,
        question,
      });
      return data;
    } catch (error) {
      handleError(error, '대화 답변 중 오류가 발생했습니다.');
    }
  },

  // 대화 내용을 Redis에서 DB로 flush
  async flushConversation(childId, topicId) {
    try {
      const { data } = await api.post(`/conversation/flush`, null, {
        params: { childId, topicId },
      });
      return data;
    } catch (error) {
      handleError(error, '대화 내용 저장 중 오류가 발생했습니다.');
    }
  },

  // ✅ 자녀 등록 API (이미지 제외: JSON만 전송) → payload만 반환
  async registerChild(userId, childData) {
    try {
      const body = {
        name: childData.name,
        birthDate: childData.birthDate,
        gender: childData.gender,
        interests: childData.interests,
      };
      const { data } = await api.post(`/users/${userId}/children`, body, {
        headers: { 'Content-Type': 'application/json' },
      });
      return data.data; // { childId, ... }
    } catch (error) {
      handleError(error, '자녀 등록 중 오류가 발생했습니다.');
    }
  },

  // ✅ 프로필 이미지 업로드 API (멀티파트) — Content-Type을 수동 지정하지 않습니다
  async uploadChildImage(userId, childId, file) {
    try {
      const form = new FormData();
      form.append('file', file); // @RequestPart("file")
      const { data } = await api.put(`/users/${userId}/children/${childId}/image`, form);
      return data; // ApiResponse<Void>
    } catch (error) {
      handleError(error, '자녀 프로필 이미지 업로드 중 오류가 발생했습니다.');
    }
  },

  // 월별 그림일기 조회 API
  async getMonthlyDiaries(childId, year, month) {
    try {
      const { data } = await api.get(`/diaries/monthly`, { params: { childId, year, month } });
      return data;
    } catch (error) {
      handleError(error, '월별 그림일기 조회 중 오류가 발생했습니다.');
    }
  },

  // ✅ 사용자의 모든 자녀 조회 API → payload만 반환 (imageUrl 포함)
  async getAllChildren(userId) {
    try {
      const { data } = await api.get(`/users/${userId}/children`);
      return data.data; // Array<Child>
    } catch (error) {
      handleError(error, '자녀 목록 조회 중 오류가 발생했습니다.');
    }
  },

  // ✅ 특정 자녀 정보 조회 API → payload만 반환 (imageUrl 포함)
  async getChild(userId, childId) {
    try {
      const { data } = await api.get(`/users/${userId}/children/${childId}`);
      return data.data; // Child
    } catch (error) {
      handleError(error, '자녀 정보 조회 중 오류가 발생했습니다.');
    }
  },

  // 자녀 정보 수정 API (⚠ 서버 DTO가 profileImg를 갖고 있으므로, 이미지 유지가 필요하면 현재 키를 함께 넘기세요)
  async updateChild(userId, childId, childData) {
    try {
      const body = {
        name: childData.name,
        birthDate: childData.birthDate,
        // 서버 DTO(ChildUpdateRequestDTO)에 gender 필드가 없다면 제외
        profileImg: childData.profileImg ?? null, // 주의: null 전송 시 서버가 null로 덮어쓸 수 있음
        interests: childData.interests,
      };
      const { data } = await api.put(`/users/${userId}/children/${childId}`, body, {
        headers: { 'Content-Type': 'application/json' },
      });
      return data;
    } catch (error) {
      handleError(error, '자녀 정보 수정 중 오류가 발생했습니다.');
    }
  },

  // 자녀 삭제 API
  async deleteChild(userId, childId) {
    try {
      const { data } = await api.delete(`/users/${userId}/children/${childId}`);
      return data;
    } catch (error) {
      handleError(error, '자녀 삭제 중 오류가 발생했습니다.');
    }
  },

  // 자녀 관심사 추가 API
  async addChildInterests(userId, childId, dataBody) {
    try {
      const { data } = await api.post(`/users/${userId}/children/${childId}/interest`, dataBody, {
        headers: { 'Content-Type': 'application/json' },
      });
      return data;
    } catch (error) {
      handleError(error, '관심사 추가 중 오류가 발생했습니다.');
    }
  },

  // 자녀 관심사 삭제 API
  async deleteChildInterests(userId, childId, dataBody) {
    try {
      const { data } = await api.delete(`/users/${userId}/children/${childId}/interest`, {
        data: dataBody,
        headers: { 'Content-Type': 'application/json' },
      });
      return data;
    } catch (error) {
      handleError(error, '관심사 삭제 중 오류가 발생했습니다.');
    }
  },
};
