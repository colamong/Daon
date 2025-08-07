import apiClient from '@/utils/axios.js';

export const userService = {
  async getCurrentUser() {
    try {
      const response = await apiClient.get(`/api/user/me`);
      console.log('사용자 정보 응답:', response.data);
      // 먼저 응답 구조를 확인해보고 적절히 수정
      return response.data.data || response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      if (error.response?.status === 401) {
        throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
      }
      
      throw new Error('사용자 정보를 가져오는 중 오류가 발생했습니다.');
    }
  },

  async updateProfile(profileData) {
    try {
      const response = await apiClient.put(`/api/user/profile`, profileData);
      return response.data;
    } catch (error) {
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      if (error.response?.status === 401) {
        throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
      }
      
      if (error.response) {
        const message = error.response.data?.message || '프로필 수정 중 오류가 발생했습니다.';
        throw new Error(message);
      }
      
      throw new Error('네트워크 오류가 발생했습니다.');
    }
  }
};