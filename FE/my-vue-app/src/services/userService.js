import apiClient from '@/utils/axios.js';

export const userService = {
  // 내 정보 조회 (기존 함수)
  async getCurrentUser() {
    try {
      const response = await apiClient.get(`/api/user/me`);
      console.log('사용자 정보 응답:', response.data);
      return response.data.data || response.data;
    } catch (error) {
      console.log(error.code);
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      if (error.response?.status === 401) {
        throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
      }
      throw new Error('사용자 정보를 가져오는 중 오류가 발생했습니다.');
    }
  },

  // 내 정보 조회 (호환용 헬퍼)
  async me() {
    return this.getCurrentUser();
  },

  // 텍스트 프로필 수정 (닉네임/국가) 
  async updateProfile(profileData, userId) {
    try {
      // userId를 요청 본문에 포함시켜 전달
      const requestData = {
        ...profileData,
        userId: userId
      };
      const response = await apiClient.put(`/api/user/profile`, requestData);
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
  },

  // 프로필 이미지 업로드/교체 (멀티파트) 
  async uploadProfileImage(file) {
    try {
      const form = new FormData();
      form.append('file', file); // 키 이름 = "file" (서버 @RequestPart("file") 와 동일!)

      // 헤더를 명시적으로 넣지 말기! (axios가 자동으로 multipart 경계 헤더를 붙임)
      const res = await apiClient.put('/api/user/profile/image', form);
      return res.data;
    } catch (error) {
      console.error('uploadProfileImage error:', error);
      // 413 에러에 대한 명확한 안내
      if (error.response?.status === 413) {
        throw new Error('이미지 파일이 너무 큽니다. 더 작은 크기의 이미지를 선택해주세요.');
      }
      throw new Error('프로필 이미지 업로드 중 오류가 발생했습니다.');
    }
  },
};
