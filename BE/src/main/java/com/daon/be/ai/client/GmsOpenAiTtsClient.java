package com.daon.be.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmsOpenAiTtsClient {

    @Value("${gms.api.key}")
    private String gmsKey;

    // GMS 프록시 엔드포인트 (OpenAI /v1/audio/speech 래핑)
    private static final String URL = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/audio/speech";

    private final ObjectMapper om = new ObjectMapper();

    /**
     * 텍스트를 TTS로 변환하여 바로 OutputStream으로 전송
     *
     * @param text   합성할 텍스트 (최대 4096 chars / GMS 안내는 2,000토큰)
     * @param voice  예: "nova", "alloy", "verse" ...
     * @param format 예: "wav", "mp3", "opus", "aac", "flac", "pcm"
     * @param speed  0.25 ~ 4.0 (기본 1.0)
     * @param out    응답 오디오를 쓸 대상 스트림 (파일 저장 없이 바로 전송)
     */
    public void stream(String text, String voice, String format, double speed, OutputStream out) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("TTS 텍스트가 비어있습니다.");
        }
        String v = (voice == null || voice.isBlank()) ? "nova" : voice;
        String f = (format == null || format.isBlank()) ? "wav" : format;
        double s = Math.max(0.25, Math.min(4.0, speed <= 0 ? 1.0 : speed));

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini-tts");
        body.put("input", text);
        body.put("voice", v);
        body.put("response_format", f);
        body.put("speed", s);
        body.put("stream_format", "audio"); // 오디오 스트림 방식 (SSE 아님)

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(URL);
            post.addHeader("Authorization", "Bearer " + gmsKey);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(om.writeValueAsString(body), StandardCharsets.UTF_8));

            http.execute(post, res -> {
                try (InputStream in = res.getEntity().getContent()) {
                    in.transferTo(out); // 디스크 저장 없이 바로 흘려보냄
                    out.flush();
                }
                return null;
            });
        } catch (Exception e) {
            // 운영 로그에 모델/보이스/포맷/길이 정도만 남기고 본문은 민감할 수 있으니 지양
            throw new RuntimeException("GMS TTS 스트리밍 실패 (voice=%s, format=%s)".formatted(v, f), e);
        }
    }

    /**
     * 필요 시 바이트 배열로 받고 싶은 경우 (예: FastAPI로 즉시 전달)
     *  - 스트리밍 선호. 꼭 필요할 때만 사용.
     */
    public byte[] synthesize(String text, String voice, String format, double speed) {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream(1024 * 1024);
        stream(text, voice, format, speed, bos);
        return bos.toByteArray();
    }
}
