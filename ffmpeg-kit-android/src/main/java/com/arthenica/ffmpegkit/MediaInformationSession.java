/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.AbstractSession;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.LogRedirectionStrategy;
import com.arthenica.ffmpegkit.MediaInformation;
import com.arthenica.ffmpegkit.MediaInformationSessionCompleteCallback;
import com.arthenica.ffmpegkit.Session;

public class MediaInformationSession
extends AbstractSession
implements Session {
    private MediaInformation mediaInformation;
    private final MediaInformationSessionCompleteCallback completeCallback;

    public static MediaInformationSession create(String[] arguments) {
        return new MediaInformationSession(arguments, null, null);
    }

    public static MediaInformationSession create(String[] arguments, MediaInformationSessionCompleteCallback completeCallback) {
        return new MediaInformationSession(arguments, completeCallback, null);
    }

    public static MediaInformationSession create(String[] arguments, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback) {
        return new MediaInformationSession(arguments, completeCallback, logCallback);
    }

    private MediaInformationSession(String[] arguments, MediaInformationSessionCompleteCallback completeCallback, LogCallback logCallback) {
        super(arguments, logCallback, LogRedirectionStrategy.NEVER_PRINT_LOGS);
        this.completeCallback = completeCallback;
    }

    public MediaInformation getMediaInformation() {
        return this.mediaInformation;
    }

    public void setMediaInformation(MediaInformation mediaInformation) {
        this.mediaInformation = mediaInformation;
    }

    public MediaInformationSessionCompleteCallback getCompleteCallback() {
        return this.completeCallback;
    }

    @Override
    public boolean isFFmpeg() {
        return false;
    }

    @Override
    public boolean isFFprobe() {
        return false;
    }

    @Override
    public boolean isMediaInformation() {
        return true;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MediaInformationSession{");
        stringBuilder.append("sessionId=");
        stringBuilder.append(this.sessionId);
        stringBuilder.append(", createTime=");
        stringBuilder.append(this.createTime);
        stringBuilder.append(", startTime=");
        stringBuilder.append(this.startTime);
        stringBuilder.append(", endTime=");
        stringBuilder.append(this.endTime);
        stringBuilder.append(", arguments=");
        stringBuilder.append(FFmpegKitConfig.argumentsToString(this.arguments));
        stringBuilder.append(", logs=");
        stringBuilder.append(this.getLogsAsString());
        stringBuilder.append(", state=");
        stringBuilder.append((Object)this.state);
        stringBuilder.append(", returnCode=");
        stringBuilder.append(this.returnCode);
        stringBuilder.append(", failStackTrace=");
        stringBuilder.append('\'');
        stringBuilder.append(this.failStackTrace);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

