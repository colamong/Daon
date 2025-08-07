import apiClient from '@/utils/axios.js';
import { useAuthStore } from '@/store/auth';

// 항상 최신 로그인 유저 ID 가져오는 함수
function getUserId() {
  const auth = useAuthStore();
  return auth.user?.id || null;
}

// 월별 일정 조회
export async function fetchMonthlyEvents(year, month) {
  const userId = getUserId();
  if (!userId) throw new Error("로그인 정보가 없습니다.");
  // GET /api/calendar/{userId}/events/monthly?year=2025&month=8
  const res = await apiClient.get(`/api/calendar/${userId}/events/monthly`, {
    params: { year, month },
  });
  return res.data;
}

// 특정 날짜 or 전체 일정 조회
export async function fetchEvents(date = null) {
  const userId = getUserId();
  if (!userId) throw new Error("로그인 정보가 없습니다.");
  // GET /api/calendar/{userId}/events?date=YYYY-MM-DD
  const res = await apiClient.get(`/api/calendar/${userId}/events`, {
    params: date ? { date } : {},
  });
  return res.data;
}

// 단건 일정 조회
export async function fetchEventById(eventId) {
  const userId = getUserId();
  if (!userId) throw new Error("로그인 정보가 없습니다.");
  // GET /api/calendar/{userId}/events/{eventId}
  const res = await apiClient.get(`/api/calendar/${userId}/events/${eventId}`);
  return res.data;
}

// 일정 생성
export async function createEvent(eventData) {
  const userId = getUserId();
  if (!userId) throw new Error("로그인 정보가 없습니다.");
  // POST /api/calendar/{userId}/events
  const res = await apiClient.post(`/api/calendar/${userId}/events`, eventData);
  return res.data;
}

// 일정 수정
export async function updateEvent(eventId, updateData) {
  const userId = getUserId();
  if (!userId) throw new Error("로그인 정보가 없습니다.");
  // PUT /api/calendar/{userId}/events/{eventId}
  const res = await apiClient.put(`/api/calendar/${userId}/events/${eventId}`, updateData);
  return res.data;
}

// 일정 삭제
export async function deleteEvent(eventId) {
  const userId = getUserId();
  if (!userId) throw new Error("로그인 정보가 없습니다.");
  // DELETE /api/calendar/{userId}/events/{eventId}
  const res = await apiClient.delete(`/api/calendar/${userId}/events/${eventId}`);
  return res.data;
}
