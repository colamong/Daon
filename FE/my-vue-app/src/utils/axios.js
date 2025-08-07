import axios from 'axios';

// axios 인스턴스 생성 (프록시 사용을 위해 baseURL을 비워둠)
const apiClient = axios.create({
  baseURL: '',
  withCredentials: true,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    console.log('API 요청:', config.method?.toUpperCase(), config.url);
    console.log('요청 데이터:', config.data);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => {
    console.log('API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API 오류:', error.response?.status, error.config?.url);
    return Promise.reject(error);
  }
);

export default apiClient;