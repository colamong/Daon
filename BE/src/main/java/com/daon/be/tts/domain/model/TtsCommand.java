package com.daon.be.tts.domain.model;

public record TtsCommand(String text, Voice voice, AudioFormat format, Speed speed) {

    public static TtsCommand of(String text, Voice voice, AudioFormat format, Speed speed) {

        if (text == null || text.isBlank()) throw new IllegalArgumentException("텍스트 없음");
        Voice v = voice == null ? Voice.NOVA : voice;
        AudioFormat f = format == null ? AudioFormat.WAV : format;
        Speed s = speed == null ? Speed.default1x() : speed;
        return new TtsCommand(text, v, f, s);
    }
}
