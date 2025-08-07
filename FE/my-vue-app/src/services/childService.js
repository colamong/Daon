import axios from 'axios';

// Vite 프록시를 사용하여 CORS 문제 해결
const API_BASE_URL = '';

// 임시 인증 토큰 (개발용)
const TEMP_AUTH_TOKEN = 'Bearer temp-token-for-development';

// axios 기본 헤더 설정
axios.defaults.headers.common['Authorization'] = TEMP_AUTH_TOKEN;
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.withCredentials = true; // 쿠키 포함

export const childService = {
  // 아이 표정 기록 API
  async recordExpression(childId, conversationResultId) {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/children/${childId}/expressions/${conversationResultId}`, {}, {
        timeout: 30000 // 30초 타임아웃
      });
      
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      throw new Error(error.response?.data?.message || '표정 기록 중 오류가 발생했습니다.');
    }
  },

  // 다이어리 생성 API
  async createDiary(conversationResultId) {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/diaries/${conversationResultId}`, {}, {
        timeout: 30000 // 30초 타임아웃
      });
      
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      throw new Error(error.response?.data?.message || '다이어리 생성 중 오류가 발생했습니다.');
    }
  },

  // 대화 시작 API
  async startConversation(childId) {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/conversation/start?childId=${childId}`, {
        timeout: 30000 // 30초 타임아웃
      });
      
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      throw new Error(error.response?.data?.message || '대화 시작 중 오류가 발생했습니다.');
    }
  },

  // 대화 답변 API
  async sendConversationAnswer(childId, topicId, step, answer, question = '') {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/conversation/answer`, {
        childId,
        topicId,
        step,
        answer,
        question // 백엔드에서 필요할 수도 있음
      }, {
        timeout: 30000 // 30초 타임아웃
      });
      
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      throw new Error(error.response?.data?.message || '대화 답변 중 오류가 발생했습니다.');
    }
  },

  // 대화 내용을 Redis에서 DB로 flush
  async flushConversation(childId, topicId) {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/conversation/flush?childId=${childId}&topicId=${topicId}`, {}, {
        timeout: 30000 // 30초 타임아웃
      });
      
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      throw new Error(error.response?.data?.message || '대화 내용 저장 중 오류가 발생했습니다.');
    }
  }
};