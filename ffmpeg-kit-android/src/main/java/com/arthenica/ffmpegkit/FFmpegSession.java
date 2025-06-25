/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.arthenica.ffmpegkit;

import android.util.Log;
import com.arthenica.ffmpegkit.AbstractSession;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.LogRedirectionStrategy;
import com.arthenica.ffmpegkit.Session;
import com.arthenica.ffmpegkit.Statistics;
import com.arthenica.ffmpegkit.StatisticsCallback;
import java.util.LinkedList;
import java.util.List;

public class FFmpegSession
extends AbstractSession
implements Session {
    private final StatisticsCallback statisticsCallback;
    private final FFmpegSessionCompleteCallback completeCallback;
    private final List<Statistics> statistics;
    private final Object statisticsLock;

    public static FFmpegSession create(String[] arguments) {
        return new FFmpegSession(arguments, null, null, null, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFmpegSession create(String[] arguments, FFmpegSessionCompleteCallback completeCallback) {
        return new FFmpegSession(arguments, completeCallback, null, null, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFmpegSession create(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback) {
        return new FFmpegSession(arguments, completeCallback, logCallback, statisticsCallback, FFmpegKitConfig.getLogRedirectionStrategy());
    }

    public static FFmpegSession create(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, LogRedirectionStrategy logRedirectionStrategy) {
        return new FFmpegSession(arguments, completeCallback, logCallback, statisticsCallback, logRedirectionStrategy);
    }

    private FFmpegSession(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, LogRedirectionStrategy logRedirectionStrategy) {
        super(arguments, logCallback, logRedirectionStrategy);
        this.completeCallback = completeCallback;
        this.statisticsCallback = statisticsCallback;
        this.statistics = new LinkedList<Statistics>();
        this.statisticsLock = new Object();
    }

    public StatisticsCallback getStatisticsCallback() {
        return this.statisticsCallback;
    }

    public FFmpegSessionCompleteCallback getCompleteCallback() {
        return this.completeCallback;
    }

    public List<Statistics> getAllStatistics(int waitTimeout) {
        this.waitForAsynchronousMessagesInTransmit(waitTimeout);
        if (this.thereAreAsynchronousMessagesInTransmit()) {
            Log.i((String)"ffmpeg-kit", (String)String.format("getAllStatistics was called to return all statistics but there are still statistics being transmitted for session id %d.", this.sessionId));
        }
        return this.getStatistics();
    }

    public List<Statistics> getAllStatistics() {
        return this.getAllStatistics(5000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Statistics> getStatistics() {
        Object object = this.statisticsLock;
        synchronized (object) {
            return this.statistics;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Statistics getLastReceivedStatistics() {
        Object object = this.statisticsLock;
        synchronized (object) {
            if (this.statistics.size() > 0) {
                return this.statistics.get(this.statistics.size() - 1);
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addStatistics(Statistics statistics) {
        Object object = this.statisticsLock;
        synchronized (object) {
            this.statistics.add(statistics);
        }
    }

    @Override
    public boolean isFFmpeg() {
        return true;
    }

    @Override
    public boolean isFFprobe() {
        return false;
    }

    @Override
    public boolean isMediaInformation() {
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FFmpegSession{");
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

