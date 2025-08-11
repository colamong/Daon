package com.daon.be.tts.domain.model;

public enum AudioFormat {
    WAV("audio/wav", "wav"),
    MP3("audio/mpeg", "mp3");

    public final String contentType;
    public final String external;

    AudioFormat(String contentType, String external) {
        this.contentType = contentType;
        this.external = external;
    }
}


