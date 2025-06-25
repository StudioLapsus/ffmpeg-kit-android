/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

public class Statistics {
    private long sessionId;
    private int videoFrameNumber;
    private float videoFps;
    private float videoQuality;
    private long size;
    private double time;
    private double bitrate;
    private double speed;

    public Statistics(long sessionId, int videoFrameNumber, float videoFps, float videoQuality, long size, double time, double bitrate, double speed) {
        this.sessionId = sessionId;
        this.videoFrameNumber = videoFrameNumber;
        this.videoFps = videoFps;
        this.videoQuality = videoQuality;
        this.size = size;
        this.time = time;
        this.bitrate = bitrate;
        this.speed = speed;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public int getVideoFrameNumber() {
        return this.videoFrameNumber;
    }

    public void setVideoFrameNumber(int videoFrameNumber) {
        this.videoFrameNumber = videoFrameNumber;
    }

    public float getVideoFps() {
        return this.videoFps;
    }

    public void setVideoFps(float videoFps) {
        this.videoFps = videoFps;
    }

    public float getVideoQuality() {
        return this.videoQuality;
    }

    public void setVideoQuality(float videoQuality) {
        this.videoQuality = videoQuality;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getBitrate() {
        return this.bitrate;
    }

    public void setBitrate(double bitrate) {
        this.bitrate = bitrate;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Statistics{");
        stringBuilder.append("sessionId=");
        stringBuilder.append(this.sessionId);
        stringBuilder.append(", videoFrameNumber=");
        stringBuilder.append(this.videoFrameNumber);
        stringBuilder.append(", videoFps=");
        stringBuilder.append(this.videoFps);
        stringBuilder.append(", videoQuality=");
        stringBuilder.append(this.videoQuality);
        stringBuilder.append(", size=");
        stringBuilder.append(this.size);
        stringBuilder.append(", time=");
        stringBuilder.append(this.time);
        stringBuilder.append(", bitrate=");
        stringBuilder.append(this.bitrate);
        stringBuilder.append(", speed=");
        stringBuilder.append(this.speed);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

