package com.daon.be.tts.domain.port;

public interface SttClientPort {
	String transcribe(byte[] audio, String filename, String language);
}
