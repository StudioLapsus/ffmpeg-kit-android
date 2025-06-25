/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.Level;

public class Log {
    private final long sessionId;
    private final Level level;
    private final String message;

    public Log(long sessionId, Level level, String message) {
        this.sessionId = sessionId;
        this.level = level;
        this.message = message;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public Level getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Log{");
        stringBuilder.append("sessionId=");
        stringBuilder.append(this.sessionId);
        stringBuilder.append(", level=");
        stringBuilder.append((Object)this.level);
        stringBuilder.append(", message=");
        stringBuilder.append("'");
        stringBuilder.append(this.message);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

