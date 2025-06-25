/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

public enum Signal {
    SIGINT(2),
    SIGQUIT(3),
    SIGPIPE(13),
    SIGTERM(15),
    SIGXCPU(24);

    private final int value;

    private Signal(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

