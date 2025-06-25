/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.AbiDetect;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.StatisticsCallback;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class FFmpegKit {
    private FFmpegKit() {
    }

    public static FFmpegSession executeWithArguments(String[] arguments) {
        FFmpegSession session = FFmpegSession.create(arguments);
        FFmpegKitConfig.ffmpegExecute(session);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback, logCallback, statisticsCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static FFmpegSession executeWithArgumentsAsync(String[] arguments, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(arguments, completeCallback, logCallback, statisticsCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static FFmpegSession execute(String command) {
        return FFmpegKit.executeWithArguments(FFmpegKitConfig.parseArguments(command));
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback) {
        return FFmpegKit.executeWithArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback);
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback) {
        return FFmpegKit.executeWithArgumentsAsync(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback, statisticsCallback);
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(FFmpegKitConfig.parseArguments(command), completeCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static FFmpegSession executeAsync(String command, FFmpegSessionCompleteCallback completeCallback, LogCallback logCallback, StatisticsCallback statisticsCallback, ExecutorService executorService) {
        FFmpegSession session = FFmpegSession.create(FFmpegKitConfig.parseArguments(command), completeCallback, logCallback, statisticsCallback);
        FFmpegKitConfig.asyncFFmpegExecute(session, executorService);
        return session;
    }

    public static void cancel() {
        FFmpegKitConfig.nativeFFmpegCancel(0L);
    }

    public static void cancel(long sessionId) {
        FFmpegKitConfig.nativeFFmpegCancel(sessionId);
    }

    public static List<FFmpegSession> listSessions() {
        return FFmpegKitConfig.getFFmpegSessions();
    }

    static {
        AbiDetect.class.getName();
        FFmpegKitConfig.class.getName();
    }
}

