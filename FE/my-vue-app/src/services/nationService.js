import apiClient from '@/utils/axios.js';

export const nationService = {
  async getNations() {
    try {
      const response = await apiClient.get(`/api/user/nations`);
      
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      if (error.response) {
        const message = error.response.data?.message || '국가 목록을 가져오는 중 오류가 발생했습니다.';
        throw new Error(message);
      }
      
      throw new Error('네트워크 오류가 발생했습니다.');
    }
  }
};