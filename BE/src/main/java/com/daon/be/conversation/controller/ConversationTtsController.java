package com.daon.be.conversation.controller;

import com.daon.be.conversation.service.ConversationTtsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 파일 저장 없이 바로 재생 가능한 TTS 스트리밍 엔드포인트
 * 프론트: <audio src="/api/conversation/tts/stream?text=...">
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversation/tts")
public class ConversationTtsController {

    private final ConversationTtsFacade ttsFacade;

    @GetMapping("/stream")
    public ResponseEntity<StreamingResponseBody> stream(
            @RequestParam String text,
            @RequestParam(defaultValue = "nova") String voice,
            @RequestParam(defaultValue = "wav") String format,
            @RequestParam(defaultValue = "1.0") double speed,
            @RequestParam(defaultValue = "false") boolean eval // true면 streamAndEval 사용
    ) {
        // Content-Type 지정 (브라우저가 바로 재생하도록)
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/" + ("wav".equalsIgnoreCase(format) ? "wav" : "mpeg"));
        headers.add(HttpHeaders.CACHE_CONTROL, "no-store");
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"speech." + format.toLowerCase() + "\"");

        StreamingResponseBody body = out -> {
            if (eval) ttsFacade.streamAndEval(text, voice, format, speed, out);
            else      ttsFacade.stream(text,      voice, format, speed, out);
        };

        return ResponseEntity.ok().headers(headers).body(body);
    }

    /** 유틸: 서버에서 안전하게 만든 스트림 URL을 제공하고 싶을 때 */
    public static String toStreamUrl(String text, String voice, String format, double speed, boolean eval) {
        String t = URLEncoder.encode(text, StandardCharsets.UTF_8);
        return "/api/conversation/tts/stream?text=" + t +
                "&voice=" + voice + "&format=" + format + "&speed=" + speed + "&eval=" + eval;
    }
}
