// 시간 포맷팅 유틸리티

// 채팅 시간 포맷 (HH:MM)
export const formatChatTime = (dateTimeString) => {
  if (!dateTimeString) return '';
  
  try {
    const date = new Date(dateTimeString);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  } catch (error) {
    return '';
  }
};

// 채팅 날짜 포맷 (MM/DD)
export const formatChatDate = (dateTimeString) => {
  if (!dateTimeString) return '';
  
  try {
    const date = new Date(dateTimeString);
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${month}/${day}`;
  } catch (error) {
    return '';
  }
};

// TypeScript 초안
export const example = () => {
};
