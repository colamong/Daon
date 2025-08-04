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

export default mock;
