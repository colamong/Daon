// ⚠️ DEPRECATED: 이 파일은 더 이상 사용되지 않습니다.
// 펭귄 성장 로직은 백엔드 API (/api/pet/{childId})에서 처리됩니다.
// 
// 백엔드 API 사용:
// - GET /api/pet/{childId} : 펭귄 상태 조회
// - POST /api/pet/reward/{childId} : 대화 후 보상 지급
//
// 아이별 펭귄 데이터 (DEPRECATED)
export const penguinDataByChild = {
  "김미래": {
    currentStage: 4,
    conversationCount: 3,  // 현재 레벨에서의 대화 횟수
    totalConversations: 20 // 전체 대화 횟수 (통계용)
  },
  "김과거": {
    currentStage: 2,
    conversationCount: 1,
    totalConversations: 6
  },
  "김현재": {
    currentStage: 6,
    conversationCount: 8,
    totalConversations: 48
  }
};

// 새로운 아이의 기본 펭귄 데이터
export const getDefaultPenguinData = () => ({
  currentStage: 1,
  conversationCount: 0,
  totalConversations: 0
});

// 아이별 펭귄 데이터 가져오기
export const getChildPenguinData = (childName) => {
  return penguinDataByChild[childName] || getDefaultPenguinData();
};

// 펭귄 데이터 업데이트 (실제로는 API 호출)
export const updateChildPenguinData = (childName, data) => {
  if (penguinDataByChild[childName]) {
    penguinDataByChild[childName] = { ...penguinDataByChild[childName], ...data };
  } else {
    penguinDataByChild[childName] = { ...getDefaultPenguinData(), ...data };
  }
  
  // 실제 구현시에는 여기서 localStorage 저장 또는 API 호출
  return penguinDataByChild[childName];
};

// 대화 횟수 증가 함수
export const incrementConversation = (childName) => {
  const currentData = getChildPenguinData(childName);
  const levelRequirements = {
    1: 2, 2: 4, 3: 6, 4: 8, 5: 10, 6: 12, 7: 0
  };
  
  const requiredForNextLevel = levelRequirements[currentData.currentStage] || 0;
  const newConversationCount = currentData.conversationCount + 1;
  const newTotalConversations = currentData.totalConversations + 1;
  
  let newStage = currentData.currentStage;
  let resetConversationCount = newConversationCount;
  
  // 레벨업 체크
  if (newConversationCount >= requiredForNextLevel && currentData.currentStage < 7) {
    newStage = currentData.currentStage + 1;
    resetConversationCount = 0; // 새 레벨에서 대화 횟수 초기화
  }
  
  return updateChildPenguinData(childName, {
    currentStage: newStage,
    conversationCount: resetConversationCount,
    totalConversations: newTotalConversations
  });
};