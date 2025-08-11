package com.daon.be.conversation.service;

import com.daon.be.ai.client.GmsOpenAiTtsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ConversationTtsFacade {

    private final GmsOpenAiTtsClient ttsClient;

    // 브라우저로 바로 스트리밍
    public void stream(String text, String voice, String format, double speed, OutputStream clientOut) {
        ttsClient.stream(text, voice, format, speed, clientOut);
    }

    // (옵션) 스트리밍 + 평가 미러링
    public void streamAndEval(String text, String voice, String format, double speed, OutputStream clientOut) {
        ByteArrayOutputStream mirror = new ByteArrayOutputStream(1024 * 1024);
        OutputStream tee = new DualOutputStream(clientOut, mirror);

        ttsClient.stream(text, voice, format, speed, tee);

        // evalClient.evaluate(mirror.toByteArray(), text); // FastAPI에 multipart 전송
    }

    // 두 OutputStream으로 동시에 쓰는 유틸
    static class DualOutputStream extends OutputStream {
        private final OutputStream a, b;
        DualOutputStream(OutputStream a, OutputStream b) { this.a = a; this.b = b; }
        @Override public void write(int x) throws java.io.IOException { a.write(x); b.write(x); }
        @Override public void write(byte[] buf, int off, int len) throws java.io.IOException {
            a.write(buf, off, len); b.write(buf, off, len);
        }
        @Override public void flush() throws java.io.IOException { a.flush(); b.flush(); }
        @Override public void close() throws java.io.IOException { a.close(); b.close(); }
    }
}
