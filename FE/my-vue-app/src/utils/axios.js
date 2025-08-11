// src/utils/axios.js
import axios from 'axios';

// 프록시 사용이면 baseURL 비움
const apiClient = axios.create({
  baseURL: '',
  withCredentials: true,
  timeout: 15000,
  // 여기서 Content-Type을 고정으로 지정하지 않는다
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    // FormData면 Content-Type 제거 (axios가 자동 셋팅)
    const isFormData = (typeof FormData !== 'undefined') && (config.data instanceof FormData);
    if (isFormData) {
      if (config.headers) {
        delete config.headers['Content-Type'];
      }
    } else {
      // JSON 전송일 때만 명시 (없으면)
      if (config.headers && !config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json';
      }
    }

    // 디버그 로그
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
