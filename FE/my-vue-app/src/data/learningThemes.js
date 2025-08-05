// 상황별 학습 테마 데이터
export const learningThemes = [
  {
    id: 'hospital',
    title: '병원',
    description: '의료진과의 대화부터 진료 상황까지 병원에서 필요한 한국어를 배워보세요.',
    image: new URL('@/assets/images/hospital.jpg', import.meta.url).href,
    chapters: [
      { id: 1, title: '접수하기', description: '병원 접수처에서 예약하고 접수하는 방법을 배워보세요.' },
      { id: 2, title: '증상 설명하기', description: '의사에게 자신의 증상을 정확히 전달하는 표현을 익혀보세요.' },
      { id: 3, title: '처방전과 약국', description: '처방전을 받고 약국에서 약을 구매하는 과정을 연습해보세요.' }
    ]
  },
  {
    id: 'government',
    title: '관공서',
    description: '민원 접수, 서류 발급 등 관공서에서 사용하는 공식적인 한국어를 익혀보세요.',
    image: new URL('@/assets/images/government.jpg', import.meta.url).href,  
    chapters: [
      { id: 1, title: '민원 접수하기', description: '관공서에서 민원을 접수하고 안내받는 과정을 배워보세요.' },
      { id: 2, title: '서류 발급 신청', description: '각종 증명서와 서류 발급을 신청하는 방법을 익혀보세요.' },
      { id: 3, title: '행정 절차 문의', description: '복잡한 행정 절차에 대해 문의하고 안내받는 표현을 연습해보세요.' }
    ]
  },
  {
    id: 'shopping',
    title: '쇼핑',
    description: '물건 구매부터 교환, 환불까지 쇼핑 상황의 다양한 표현을 학습하세요.',
    image: new URL('@/assets/images/shopping.jpg', import.meta.url).href,
    chapters: [
      { id: 1, title: '물건 찾기', description: '원하는 상품을 찾고 직원에게 문의하는 방법을 배워보세요.' },
      { id: 2, title: '가격 협상과 결제', description: '가격을 확인하고 다양한 결제 방법으로 구매하는 과정을 익혀보세요.' },
      { id: 3, title: '교환과 환불', description: '구매한 상품을 교환하거나 환불하는 절차를 연습해보세요.' }
    ]
  },
  {
    id: 'bank',
    title: '은행',
    description: '계좌 개설, 송금, 대출 상담 등 은행 업무에 필요한 한국어를 배워보세요.',
    image: new URL('@/assets/images/bank.jpg', import.meta.url).href,
    chapters: [
      { id: 1, title: '계좌 개설하기', description: '은행에서 새로운 계좌를 개설하는 절차와 필요 서류를 알아보세요.' },
      { id: 2, title: '송금과 이체', description: '국내외 송금과 계좌 이체 서비스를 이용하는 방법을 배워보세요.' },
      { id: 3, title: '대출 상담', description: '대출 상품에 대해 상담받고 신청하는 과정을 익혀보세요.' }
    ]
  },
  {
    id: 'realestate',
    title: '부동산',
    description: '집 구하기부터 계약까지 부동산 거래에 필요한 실용적인 표현을 익히세요.',
    image: new URL('@/assets/images/property.jpg', import.meta.url).href,
    chapters: [
      { id: 1, title: '매물 문의하기', description: '부동산에서 원하는 조건의 집을 찾고 문의하는 방법을 배워보세요.' },
      { id: 2, title: '집 구경하기', description: '실제 매물을 보러 가서 확인해야 할 사항들을 알아보세요.' },
      { id: 3, title: '계약 체결하기', description: '임대차 계약서 작성과 계약 체결 과정을 연습해보세요.' }
    ]
  },
  {
    id: 'restaurant',
    title: '식당',
    description: '메뉴 주문부터 계산까지 식당에서 자주 사용하는 표현들을 연습해보세요.',
    image: new URL('@/assets/images/restaurant.jpg', import.meta.url).href,
    chapters: [
      { id: 1, title: '자리 잡고 메뉴 보기', description: '식당에 들어가서 자리를 잡고 메뉴를 확인하는 과정을 배워보세요.' },
      { id: 2, title: '주문하기', description: '음식과 음료를 주문하고 특별 요청사항을 전달하는 방법을 익혀보세요.' },
      { id: 3, title: '계산하기', description: '식사 후 계산을 요청하고 결제하는 과정을 연습해보세요.' }
    ]
  }
];