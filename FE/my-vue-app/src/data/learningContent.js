// 상황별 학습 콘텐츠 데이터
export const learningContent = {
  // 병원
  hospital: {
    1: [ // 접수하기 - 병원에 도착해서 접수하는 전체 과정
      {
        id: 1,
        situation: "병원에 처음 와서 접수처에 도착했을 때",
        question: "처음 오시는 분이세요?",
        questionPronunciation: "Cheo-eum o-si-neun bun-i-se-yo?",
        answers: [
          {
            id: 1,
            text: "네, 처음 왔습니다",
            pronunciation: "Ne, cheo-eum wass-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "커피 한 잔 주세요",
            pronunciation: "Keo-pi han jan ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "몇 시에 출발해요?",
            pronunciation: "Myeot si-e chul-bal-hae-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "접수원이 신분증을 요청할 때",
        question: "신분증 좀 보여주세요",
        questionPronunciation: "Sin-bun-jeung jom bo-yeo-ju-se-yo",
        answers: [
          {
            id: 1,
            text: "네, 여기 있습니다",
            pronunciation: "Ne, yeo-gi iss-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "라면 맛있게 드세요",
            pronunciation: "Ra-myeon ma-sit-ge deu-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "집에 가고 싶어요",
            pronunciation: "Jib-e ga-go sip-eo-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "접수가 완료되고 대기 안내를 받을 때",
        question: "접수 완료됐습니다. 저쪽에서 기다려 주세요",
        questionPronunciation: "Jeop-su wan-ryo-dwaess-seub-ni-da. Jeo-jjok-e-seo gi-da-ryeo ju-se-yo",
        answers: [
          {
            id: 1,
            text: "네, 알겠습니다",
            pronunciation: "Ne, al-gess-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "피자 하나 주세요",
            pronunciation: "Pi-ja ha-na ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "날씨가 좋네요",
            pronunciation: "Nal-ssi-ga jon-ne-yo",
            isCorrect: false
          }
        ]
      }
    ],
    2: [ // 증상 설명하기 - 진료실에서 의사와 대화하는 과정
      {
        id: 1,
        situation: "진료실에 들어가서 의사가 증상을 물어볼 때",
        question: "어디가 아프세요?",
        questionPronunciation: "Eo-di-ga a-peu-se-yo?",
        answers: [
          {
            id: 1,
            text: "목이 아파요",
            pronunciation: "Mok-i a-pa-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "라면 하나 주세요",
            pronunciation: "Ra-myeon ha-na ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "몇 시에 가요?",
            pronunciation: "Myeot si-e ga-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "의사가 증상의 시작 시점을 물어볼 때",
        question: "언제부터 목이 아팠어요?",
        questionPronunciation: "Eon-je-bu-teo mok-i a-pass-eo-yo?",
        answers: [
          {
            id: 1,
            text: "3일 전부터요",
            pronunciation: "Sam-il jeon-bu-teo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "커피가 맛있어요",
            pronunciation: "Keo-pi-ga ma-sit-eo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "어디에 있어요?",
            pronunciation: "Eo-di-e iss-eo-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "의사가 다른 증상이 있는지 확인할 때",
        question: "열은 나지 않나요?",
        questionPronunciation: "Yeol-eun na-ji anh-na-yo?",
        answers: [
          {
            id: 1,
            text: "네, 조금 열이 나요",
            pronunciation: "Ne, jo-geum yeol-i na-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "은행이 어디예요?",
            pronunciation: "Eun-haeng-i eo-di-ye-yo?",
            isCorrect: false
          },
          {
            id: 3,
            text: "계산해 주세요",
            pronunciation: "Gye-san-hae ju-se-yo",
            isCorrect: false
          }
        ]
      }
    ],
    3: [ // 처방전과 약국
      {
        id: 1,
        situation: "처방전을 받을 때",
        question: "처방전 드릴게요",
        questionPronunciation: "Cheo-bang-jeon deu-ril-ge-yo",
        answers: [
          {
            id: 1,
            text: "감사합니다",
            pronunciation: "Gam-sa-hab-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "머리가 아파요",
            pronunciation: "Meo-ri-ga a-pa-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "여기서 기다려요",
            pronunciation: "Yeo-gi-seo gi-da-ryeo-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "약국에서 약을 받을 때",
        question: "처방전 있으세요?",
        questionPronunciation: "Cheo-bang-jeon iss-eu-se-yo?",
        answers: [
          {
            id: 1,
            text: "네, 여기 있습니다",
            pronunciation: "Ne, yeo-gi iss-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "배가 고파요",
            pronunciation: "Bae-ga go-pa-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "오늘 날씨가 좋아요",
            pronunciation: "O-neul nal-ssi-ga jo-a-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "약 복용법을 들을 때",
        question: "하루에 세 번 드세요",
        questionPronunciation: "Ha-ru-e se beon deu-se-yo",
        answers: [
          {
            id: 1,
            text: "네, 알겠습니다",
            pronunciation: "Ne, al-gess-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "어디에 있어요?",
            pronunciation: "Eo-di-e iss-eo-yo?",
            isCorrect: false
          },
          {
            id: 3,
            text: "얼마예요?",
            pronunciation: "Eol-ma-ye-yo?",
            isCorrect: false
          }
        ]
      }
    ]
  },
  
  // 관공서
  government: {
    1: [ // 민원 접수하기
      {
        id: 1,
        situation: "관공서에서 업무를 문의할 때",
        question: "무엇을 도와드릴까요?",
        questionPronunciation: "Mu-eot-eul do-wa-deu-ril-kka-yo?",
        answers: [
          {
            id: 1,
            text: "주민등록증을 발급받고 싶어요",
            pronunciation: "Ju-min-deung-rok-jeung-eul bal-geub-ba-go sip-eo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "커피 한 잔 주세요",
            pronunciation: "Keo-pi han jan ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "화장실이 어디예요?",
            pronunciation: "Hwa-jang-sil-i eo-di-ye-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "필요한 서류를 문의할 때",
        question: "어떤 서류가 필요하죠?",
        questionPronunciation: "Eo-tteon seo-ryu-ga pil-yo-ha-jyo?",
        answers: [
          {
            id: 1,
            text: "신분증이 필요합니다",
            pronunciation: "Sin-bun-jeung-i pil-yo-hab-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "맛있게 드세요",
            pronunciation: "Ma-sit-ge deu-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "안녕히 가세요",
            pronunciation: "An-nyeong-hi ga-se-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "접수 번호를 받을 때",
        question: "번호표 뽑으시고 기다려 주세요",
        questionPronunciation: "Beon-ho-pyo ppop-eu-si-go gi-da-ryeo ju-se-yo",
        answers: [
          {
            id: 1,
            text: "네, 알겠습니다",
            pronunciation: "Ne, al-gess-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "얼마예요?",
            pronunciation: "Eol-ma-ye-yo?",
            isCorrect: false
          },
          {
            id: 3,
            text: "여기 앉아도 돼요?",
            pronunciation: "Yeo-gi an-ja-do dwae-yo?",
            isCorrect: false
          }
        ]
      }
    ],
    2: [ // 서류 발급 신청
      {
        id: 1,
        situation: "서류 발급을 신청할 때",
        question: "어떤 증명서를 발급받으시나요?",
        questionPronunciation: "Eo-tteon jeung-myeong-seo-reul bal-geub-ba-deu-si-na-yo?",
        answers: [
          {
            id: 1,
            text: "가족관계증명서 부탁드려요",
            pronunciation: "Ga-jok-gwan-gye-jeung-myeong-seo bu-tak-deu-ryeo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "라면 하나 주세요",
            pronunciation: "Ra-myeon ha-na ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "오늘 날씨 좋네요",
            pronunciation: "O-neul nal-ssi jon-ne-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "수수료를 확인할 때",
        question: "수수료는 1000원입니다",
        questionPronunciation: "Su-su-ryo-neun cheon-won-ib-ni-da",
        answers: [
          {
            id: 1,
            text: "네, 여기 있습니다",
            pronunciation: "Ne, yeo-gi iss-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "맛없어요",
            pronunciation: "Ma-seobs-eo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "몇 시예요?",
            pronunciation: "Myeot si-ye-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "발급 완료 시",
        question: "서류 나왔습니다",
        questionPronunciation: "Seo-ryu na-wass-seub-ni-da",
        answers: [
          {
            id: 1,
            text: "감사합니다",
            pronunciation: "Gam-sa-hab-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "어디 가세요?",
            pronunciation: "Eo-di ga-se-yo?",
            isCorrect: false
          },
          {
            id: 3,
            text: "뭐 하세요?",
            pronunciation: "Mwo ha-se-yo?",
            isCorrect: false
          }
        ]
      }
    ],
    3: [ // 행정 절차 문의
      {
        id: 1,
        situation: "복잡한 절차를 문의할 때",
        question: "이사신고는 어떻게 하나요?",
        questionPronunciation: "I-sa-sin-go-neun eo-tteo-ke ha-na-yo?",
        answers: [
          {
            id: 1,
            text: "전입신고서를 작성하시면 됩니다",
            pronunciation: "Jeon-ib-sin-go-seo-reul jak-seong-ha-si-myeon doeb-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "맛있어 보여요",
            pronunciation: "Ma-sit-eo bo-yeo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "길이 막혀요",
            pronunciation: "Gil-i mak-hyeo-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "필요한 기간을 문의할 때",
        question: "며칠 정도 걸리나요?",
        questionPronunciation: "Myeo-chil jeong-do geol-ri-na-yo?",
        answers: [
          {
            id: 1,
            text: "일주일 정도 걸립니다",
            pronunciation: "Il-ju-il jeong-do geol-lib-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "배가 불러요",
            pronunciation: "Bae-ga bul-leo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "집에 가고 싶어요",
            pronunciation: "Jib-e ga-go sip-eo-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "추가 정보를 문의할 때",
        question: "온라인으로도 할 수 있나요?",
        questionPronunciation: "On-la-in-eu-ro-do hal su iss-na-yo?",
        answers: [
          {
            id: 1,
            text: "네, 인터넷으로도 가능해요",
            pronunciation: "Ne, in-teo-net-eu-ro-do ga-neung-hae-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "피곤해요",
            pronunciation: "Pi-gon-hae-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "뭐 먹을까요?",
            pronunciation: "Mwo meog-eul-kka-yo?",
            isCorrect: false
          }
        ]
      }
    ]
  },
  
  // 쇼핑
  shopping: {
    1: [ // 물건 찾기
      {
        id: 1,
        situation: "상품을 찾을 때",
        question: "뭘 찾고 계세요?",
        questionPronunciation: "Mwol chat-go gye-se-yo?",
        answers: [
          {
            id: 1,
            text: "티셔츠를 찾고 있어요",
            pronunciation: "Ti-syeo-cheu-reul chat-go iss-eo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "배가 고파요",
            pronunciation: "Bae-ga go-pa-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "몇 시예요?",
            pronunciation: "Myeot si-ye-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "사이즈를 문의할 때",
        question: "어떤 사이즈요?",
        questionPronunciation: "Eo-tteon sa-i-jeu-yo?",
        answers: [
          {
            id: 1,
            text: "미디움 사이즈요",
            pronunciation: "Mi-di-eom sa-i-jeu-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "라면 주세요",
            pronunciation: "Ra-myeon ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "안녕하세요",
            pronunciation: "An-nyeong-ha-se-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "색상을 확인할 때",
        question: "다른 색깔도 있나요?",
        questionPronunciation: "Da-reun saek-kkal-do iss-na-yo?",
        answers: [
          {
            id: 1,
            text: "네, 검정색도 있어요",
            pronunciation: "Ne, geom-jeong-saek-do iss-eo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "화장실이 어디예요?",
            pronunciation: "Hwa-jang-sil-i eo-di-ye-yo?",
            isCorrect: false
          },
          {
            id: 3,
            text: "감사합니다",
            pronunciation: "Gam-sa-hab-ni-da",
            isCorrect: false
          }
        ]
      }
    ]
  },

  // 은행
  bank: {
    1: [ // 계좌 개설하기
      {
        id: 1,
        situation: "은행에서 업무를 문의할 때",
        question: "어떤 업무로 오셨나요?",
        questionPronunciation: "Eo-tteon eom-mu-ro o-syeoss-na-yo?",
        answers: [
          {
            id: 1,
            text: "계좌를 만들고 싶어요",
            pronunciation: "Gye-jwa-reul man-deul-go sip-eo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "커피 주세요",
            pronunciation: "Keo-pi ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "날씨가 좋네요",
            pronunciation: "Nal-ssi-ga jon-ne-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "필요한 서류를 확인할 때",
        question: "신분증 가져오셨나요?",
        questionPronunciation: "Sin-bun-jeung ga-jyeo-o-syeoss-na-yo?",
        answers: [
          {
            id: 1,
            text: "네, 여기 있습니다",
            pronunciation: "Ne, yeo-gi iss-seub-ni-da",
            isCorrect: true
          },
          {
            id: 2,
            text: "맛있어요",
            pronunciation: "Ma-sit-eo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "어디 가세요?",
            pronunciation: "Eo-di ga-se-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "계좌 종류를 선택할 때",
        question: "어떤 계좌를 만드시겠어요?",
        questionPronunciation: "Eo-tteon gye-jwa-reul man-deu-si-gess-eo-yo?",
        answers: [
          {
            id: 1,
            text: "적금 계좌 부탁드려요",
            pronunciation: "Jeok-geum gye-jwa bu-tak-deu-ryeo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "라면 먹고 싶어요",
            pronunciation: "Ra-myeon meok-go sip-eo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "집에 가고 싶어요",
            pronunciation: "Jib-e ga-go sip-eo-yo",
            isCorrect: false
          }
        ]
      }
    ]
  },

  // 부동산
  realestate: {
    1: [ // 매물 문의하기
      {
        id: 1,
        situation: "부동산에서 집을 찾을 때",
        question: "어떤 집을 찾고 계세요?",
        questionPronunciation: "Eo-tteon jib-eul chat-go gye-se-yo?",
        answers: [
          {
            id: 1,
            text: "원룸을 찾고 있어요",
            pronunciation: "Won-rum-eul chat-go iss-eo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "피자 주문하고 싶어요",
            pronunciation: "Pi-ja ju-mun-ha-go sip-eo-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "몇 시에 출발해요?",
            pronunciation: "Myeot si-e chul-bal-hae-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "예산을 확인할 때",
        question: "예산은 어느 정도 생각하세요?",
        questionPronunciation: "Ye-san-eun eo-neu jeong-do saeng-gak-ha-se-yo?",
        answers: [
          {
            id: 1,
            text: "월세 50만원 정도요",
            pronunciation: "Wol-se o-sip-man-won jeong-do-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "오늘 비 온대요",
            pronunciation: "O-neul bi on-dae-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "배고파요",
            pronunciation: "Bae-go-pa-yo",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "지역을 확인할 때",
        question: "어느 지역을 원하세요?",
        questionPronunciation: "Eo-neu ji-yeok-eul won-ha-se-yo?",
        answers: [
          {
            id: 1,
            text: "강남 쪽으로 부탁드려요",
            pronunciation: "Gang-nam jjok-eu-ro bu-tak-deu-ryeo-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "감사합니다, 안녕히 가세요",
            pronunciation: "Gam-sa-hab-ni-da, an-nyeong-hi ga-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "화장실이 어디예요?",
            pronunciation: "Hwa-jang-sil-i eo-di-ye-yo?",
            isCorrect: false
          }
        ]
      }
    ]
  },

  // 식당
  restaurant: {
    1: [ // 자리 잡고 메뉴 보기
      {
        id: 1,
        situation: "식당에 들어갈 때",
        question: "몇 분이세요?",
        questionPronunciation: "Myeot bun-i se-yo?",
        answers: [
          {
            id: 1,
            text: "두 명이요",
            pronunciation: "Du myeong-i-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "계산서 주세요",
            pronunciation: "Gye-san-seo ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "화장실이 어디예요?",
            pronunciation: "Hwa-jang-sil-i eo-di-ye-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 2,
        situation: "메뉴를 추천받을 때",
        question: "뭐가 맛있어요?",
        questionPronunciation: "Mwo-ga ma-sit-eo-yo?",
        answers: [
          {
            id: 1,
            text: "김치찌개가 인기 메뉴예요",
            pronunciation: "Gim-chi-jji-gae-ga in-gi me-nyu-ye-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "몇 시예요?",
            pronunciation: "Myeot si-ye-yo?",
            isCorrect: false
          },
          {
            id: 3,
            text: "은행이 어디예요?",
            pronunciation: "Eun-haeng-i eo-di-ye-yo?",
            isCorrect: false
          }
        ]
      },
      {
        id: 3,
        situation: "음료를 추천받을 때",
        question: "음료는 뭘로 하시겠어요?",
        questionPronunciation: "Eum-ryo-neun mwol-lo ha-si-gess-eo-yo?",
        answers: [
          {
            id: 1,
            text: "콜라 주세요",
            pronunciation: "Kol-la ju-se-yo",
            isCorrect: true
          },
          {
            id: 2,
            text: "주민등록증 주세요",
            pronunciation: "Ju-min-deung-rok-jeung ju-se-yo",
            isCorrect: false
          },
          {
            id: 3,
            text: "머리가 아파요",
            pronunciation: "Meo-ri-ga a-pa-yo",
            isCorrect: false
          }
        ]
      }
    ]
  }
};