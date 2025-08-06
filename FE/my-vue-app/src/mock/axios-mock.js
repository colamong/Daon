// src/mock/axios-mock.js
import axios from "axios";
import MockAdapter from "axios-mock-adapter";

const mock = new MockAdapter(axios, { delayResponse: 500 });

// 로그인 엔드포인트 모킹
mock.onPost("/api/login").reply(({ data }) => {
  const { email } = JSON.parse(data);
  return [
    200,
    {
      token: "mock-token-xyz",
      user: { email, name: "Mock User" },
    },
  ];
});

// 내 정보 조회
mock.onGet("/api/me").reply(200, {
  user: { email: "mock@example.com", name: "Mock User" },
});

// OCR 파일 업로드 및 처리
mock.onPost("/api/ocr/upload").reply(() => {
  return [
    200,
    {
      statusCode: 200,
      message: "OCR 및 쉬운말 변환 성공",
      data: {
        originalText: "안녕하세요! 이것은 복잡한 문서의 내용입니다. 법적 용어와 어려운 표현들이 포함되어 있습니다.",
        easyText: "안녕하세요! 이것은 어려운 문서를 쉽게 바꾼 내용입니다. 어려운 말들을 쉬운 말로 바꿨습니다.",
        summary: "문서의 주요 내용을 요약하면, 복잡한 내용을 쉬운 말로 바꾸는 서비스에 대한 안내입니다.",
        confidence: 0.95,
        processingTime: 1200
      }
    }
  ];
});

export default mock;
