// 시간 포맷팅 유틸리티
import dayjs from '@/utils/dayjs'

// UTC 시간을 한국 시간으로 변환하는 유틸리티 함수
export const toKoreanTime = (utcDateString) => {
  if (!utcDateString) return null;
  return dayjs(utcDateString).tz('Asia/Seoul');
};

// 채팅 시간 포맷 (HH:MM) - UTC를 한국 시간으로 변환
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

// 채팅 날짜 포맷 (MM/DD) - UTC를 한국 시간으로 변환
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
