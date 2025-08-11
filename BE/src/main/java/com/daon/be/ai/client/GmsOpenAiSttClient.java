package com.daon.be.ai.client;

import com.daon.be.tts.domain.port.SttClientPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Timeout;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmsOpenAiSttClient implements SttClientPort {

	@Value("${gms.api.key}")
	private String gmsKey;

	private static final String URL =
		"https://gms.ssafy.io/gmsapi/api.openai.com/v1/audio/transcriptions";

	private final ObjectMapper om = new ObjectMapper();

	public String transcribe(byte[] audio, String filename, String language) {
		RequestConfig cfg = RequestConfig.custom()
			.setConnectTimeout(Timeout.ofSeconds(10))
			.setResponseTimeout(Timeout.ofSeconds(90))
			.build();

		try (CloseableHttpClient http = HttpClients.custom()
			.disableCookieManagement() // 쿠키 경고 차단
			.setDefaultRequestConfig(cfg)
			.build()) {

			HttpPost post = new HttpPost(URL);
			post.addHeader("Authorization", "Bearer " + gmsKey);

			MultipartEntityBuilder mb = MultipartEntityBuilder.create()
				.setCharset(StandardCharsets.UTF_8)
				.addBinaryBody("file", audio,
					ContentType.DEFAULT_BINARY, (filename == null ? "audio.webm" : filename))
				.addTextBody("model", "whisper-1",
					ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
				.addTextBody("response_format", "json",
					ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));

			if (language != null && !language.isBlank()) {
				mb.addTextBody("language", language,
					ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8)); // 예: "ko"
			}

			post.setEntity(mb.build());

			return http.execute(post, res -> {
				int code = res.getCode();
				String body = new String(res.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
				if (code >= 200 && code < 300) {
					JsonNode node = om.readTree(body);
					return node.has("text") ? node.get("text").asText() : "";
				}
				log.warn("[STT] http={} body={}", code, body);
				throw new RuntimeException("STT 실패 (" + code + ")");
			});

		} catch (Exception e) {
			throw new RuntimeException("GMS STT 호출 실패", e);
		}
	}

	// 편의 오버로드
	public String transcribe(MultipartFile file, String language) {
		try {
			return transcribe(file.getBytes(), file.getOriginalFilename(), language);
		} catch (Exception e) {
			throw new RuntimeException("STT 파일 처리 실패", e);
		}
	}
}
