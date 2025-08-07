import apiClient from '@/utils/axios.js';

export const ocrService = {
  async uploadAndOcr(file) {
    const formData = new FormData();
    formData.append('image', file);
    
    try {      
      const response = await apiClient.post(`/api/ocr/extract`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        timeout: 30000, // 30초 타임아웃
        withCredentials: true // 쿠키 포함
      });
      
      return response.data;
    } catch (error) {
      
      if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      }
      
      throw new Error(error.response?.data?.message || 'OCR 처리 중 오류가 발생했습니다.');
    }
  }
};