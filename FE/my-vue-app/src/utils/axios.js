import axios from 'axios';

// Vite 프록시 사용 여부 (예: VITE_USE_PROXY=true)
// 프록시를 쓰면 baseURL을 비워두고, 안 쓰면 환경변수나 기본값(8080) 사용
const USE_PROXY = import.meta?.env?.VITE_USE_PROXY === 'true';
const BASE_URL = USE_PROXY
  ? '' // 예: 프론트에서 /api → 백엔드로 프록시
  : (import.meta?.env?.VITE_API_BASE_URL || 'http://localhost:8080');

// axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  timeout: 15000,
  // 기본 Content-Type은 고정하지 않음 (FormData 업로드 깨짐 방지)
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    // FormData면 Content-Type 제거(axios가 자동 설정)
    const isFormData = (typeof FormData !== 'undefined') && (config.data instanceof FormData);
    if (isFormData) {
      if (config.headers) delete config.headers['Content-Type'];
    } else {
      // JSON 전송일 때만 없으면 지정
      if (config.headers && !config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json';
      }
    }

    // 디버그
    console.log('API 요청:', (config.method || 'GET').toUpperCase(), config.url);
    console.log('요청 데이터:', isFormData ? '[FormData]' : config.data);
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => {
    console.log('API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API 오류:', error?.response?.status, error?.config?.url);
    return Promise.reject(error);
  }
);

export default apiClient;
