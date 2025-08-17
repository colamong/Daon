// 시간 포맷팅 유틸리티
import dayjs from '@/utils/dayjs'

// 서버 시간에서 9시간을 빼서 한국 시간으로 맞추는 함수
export const toKoreanTime = (serverDateString) => {
  if (!serverDateString) return null;
  // 서버 시간에서 9시간을 빼서 한국 시간으로 맞춤
  return dayjs(serverDateString).subtract(9, 'hour');
};

// 채팅 시간 포맷 (HH:MM) - 서버 시간에서 9시간을 빼서 한국 시간으로 표시
export const formatChatTime = (dateTimeString) => {
  if (!dateTimeString) return '';
  
  try {
    const koreanTime = toKoreanTime(dateTimeString);
    return koreanTime.format('HH:mm');
  } catch (error) {
    console.error('시간 포맷팅 오류:', error);
    return '';
  }
};

// 채팅 날짜 포맷 (MM/DD) - 서버 시간에서 9시간을 빼서 한국 시간으로 표시
export const formatChatDate = (dateTimeString) => {
  if (!dateTimeString) return '';
  
  try {
    const koreanTime = toKoreanTime(dateTimeString);
    return koreanTime.format('MM/DD');
  } catch (error) {
    console.error('날짜 포맷팅 오류:', error);
    return '';
  }
};

// TypeScript 초안
export const example = () => {
  console.log('Hello from TS');
};
