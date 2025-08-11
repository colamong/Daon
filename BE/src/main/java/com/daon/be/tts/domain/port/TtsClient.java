package com.daon.be.tts.domain.port;

import java.io.OutputStream;

public interface TtsClient {

    // 파일 저장 없이 바로 out 으로 흘려보냄
    void stream(String text, String voice, String format, double speed, OutputStream out);
}