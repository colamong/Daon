// 상황별 학습 테마 데이터
export const learningThemes = [
  {
    id: 'hospital',
    title: '병원',
    description: '의료진과의 대화부터 진료 상황까지 병원에서 필요한 한국어를 배워보세요.',
    image: new URL('@/assets/images/hospital.jpg', import.meta.url).href,
    chapters: []
  },
  {
    id: 'government',
    title: '관공서',
    description: '민원 접수, 서류 발급 등 관공서에서 사용하는 공식적인 한국어를 익혀보세요.',
    image: new URL('@/assets/images/government.jpg', import.meta.url).href,
    chapters: []
  },
  {
    id: 'shopping',
    title: '쇼핑',
    description: '물건 구매부터 교환, 환불까지 쇼핑 상황의 다양한 표현을 학습하세요.',
    image: new URL('@/assets/images/shopping.jpg', import.meta.url).href,
    chapters: []
  },
  {
    id: 'bank',
    title: '은행',
    description: '계좌 개설, 송금, 대출 상담 등 은행 업무에 필요한 한국어를 배워보세요.',
    image: new URL('@/assets/images/bank.jpg', import.meta.url).href,
    chapters: []
  },
  {
    id: 'realestate',
    title: '부동산',
    description: '집 구하기부터 계약까지 부동산 거래에 필요한 실용적인 표현을 익히세요.',
    image: new URL('@/assets/images/property.jpg', import.meta.url).href,
    chapters: []
  },
  {
    id: 'restaurant',
    title: '식당',
    description: '메뉴 주문부터 계산까지 식당에서 자주 사용하는 표현들을 연습해보세요.',
    image: new URL('@/assets/images/restaurant.jpg', import.meta.url).href,
    chapters: []
  }
];