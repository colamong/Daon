package com.daon.be.tts.domain.model;

public record Speed(double value) {
    public Speed {
        value = Math.max(0.25, Math.min(4.0, value));
    }
    public static Speed default1x() {
        return new Speed(1.0);
    }
}
