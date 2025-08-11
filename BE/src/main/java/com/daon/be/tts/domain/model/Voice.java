package com.daon.be.tts.domain.model;

public enum Voice {

    ALLOY, ASH, BALLAD, CORAL, ECHO, FABLE, ONYX, NOVA, SAGE, SHIMMER, VERSE;
    public String external() {
        return name().toLowerCase();
    }
}
