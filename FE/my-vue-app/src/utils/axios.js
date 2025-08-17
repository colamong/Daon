// FE/my-vue-app/src/utils/axios.js
import axios from 'axios';

/**
 * 기본 정책:
 * - 기본 baseURL은 항상 '/api' (dev/prod 공통, .env 없어도 안전)
 * - 필요할 때만 .env에서 VITE_API_BASE_URL로 덮어쓰기 가능
 *   예) VITE_API_BASE_URL=https://api.example.com
 */
const rawBase = import.meta?.env?.VITE_API_BASE_URL ?? '/api';
// 끝 슬래시 정리: '/api/' -> '/api'
const baseURL = typeof rawBase === 'string' ? rawBase.replace(/\/+$/, '') : '/api';

const apiClient = axios.create({
  baseURL,            // '/api' 또는 'https://api.example.com'
  withCredentials: true,
  timeout: 15000,
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    // 호환성 유지:
    // 서비스 코드가 '/api/...'로 호출해도 baseURL이 '/api'면 중복을 제거해 최종 '/api/...' 한 번만 되게 처리
    if (typeof config.url === 'string' && baseURL === '/api' && config.url.startsWith('/api/')) {
      config.url = config.url.slice(4); // '/api' prefix 제거
    }

    // FormData면 Content-Type 제거(axios가 자동 설정)
    const isFormData = (typeof FormData !== 'undefined') && (config.data instanceof FormData);
    if (isFormData) {
      if (config.headers) delete config.headers['Content-Type'];
    } else if (config.headers && !config.headers['Content-Type']) {
      // JSON 전송일 때만 기본값 지정
      config.headers['Content-Type'] = 'application/json';
    }

    // (옵션) 디버그 로그 유지 원하면 주석 해제
    // console.log('API 요청:', (config.method || 'GET').toUpperCase(), `${baseURL}${config.url}`);

    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => {
    // console.log('API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    // console.error('API 오류:', error?.response?.status, error?.config?.url);
    
    // 401 또는 403 에러 시 토큰 만료 처리
    if (error?.response?.status === 401 || error?.response?.status === 403) {
      // 동적 import로 순환 참조 방지
      import('@/store/auth').then(({ useAuthStore }) => {
        const authStore = useAuthStore();
        authStore.handleTokenExpired();
      }).catch(() => {
        // store 접근 실패 시 직접 localStorage 정리
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
        if (typeof window !== 'undefined') {
          window.location.href = '/';
        }
      });
    }
    
    return Promise.reject(error);
  }
);

export default apiClient;
