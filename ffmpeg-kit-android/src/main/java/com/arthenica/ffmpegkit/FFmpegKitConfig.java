/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.database.Cursor
 *  android.net.Uri
 *  android.os.Build$VERSION
 *  android.os.ParcelFileDescriptor
 *  android.util.Log
 *  android.util.SparseArray
 *  com.arthenica.smartexception.java.Exceptions
 */
package com.arthenica.ffmpegkit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.SparseArray;
import com.arthenica.ffmpegkit.Abi;
import com.arthenica.ffmpegkit.AbiDetect;
import com.arthenica.ffmpegkit.AsyncFFmpegExecuteTask;
import com.arthenica.ffmpegkit.AsyncFFprobeExecuteTask;
import com.arthenica.ffmpegkit.AsyncGetMediaInformationTask;
import com.arthenica.ffmpegkit.CameraSupport;
import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.FFprobeSession;
import com.arthenica.ffmpegkit.FFprobeSessionCompleteCallback;
import com.arthenica.ffmpegkit.Level;
import com.arthenica.ffmpegkit.Log;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.LogRedirectionStrategy;
import com.arthenica.ffmpegkit.MediaInformation;
import com.arthenica.ffmpegkit.MediaInformationJsonParser;
import com.arthenica.ffmpegkit.MediaInformationSession;
import com.arthenica.ffmpegkit.MediaInformationSessionCompleteCallback;
import com.arthenica.ffmpegkit.NativeLoader;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.Session;
import com.arthenica.ffmpegkit.SessionState;
import com.arthenica.ffmpegkit.Signal;
import com.arthenica.ffmpegkit.Statistics;
import com.arthenica.ffmpegkit.StatisticsCallback;
import com.arthenica.smartexception.java.Exceptions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FFmpegKitConfig {
    static final String TAG = "ffmpeg-kit";
    static final String FFMPEG_KIT_NAMED_PIPE_PREFIX = "fk_pipe_";
    private static final AtomicInteger uniqueIdGenerator;
    private static Level activeLogLevel;
    private static int sessionHistorySize;
    private static final Map<Long, Session> sessionHistoryMap;
    private static final List<Session> sessionHistoryList;
    private static final Object sessionHistoryLock;
    private static int asyncConcurrencyLimit;
    private static ExecutorService asyncExecutorService;
    private static LogCallback globalLogCallback;
    private static StatisticsCallback globalStatisticsCallback;
    private static FFmpegSessionCompleteCallback globalFFmpegSessionCompleteCallback;
    private static FFprobeSessionCompleteCallback globalFFprobeSessionCompleteCallback;
    private static MediaInformationSessionCompleteCallback globalMediaInformationSessionCompleteCallback;
    private static final SparseArray<SAFProtocolUrl> safIdMap;
    private static final SparseArray<SAFProtocolUrl> safFileDescriptorMap;
    private static LogRedirectionStrategy globalLogRedirectionStrategy;

    private FFmpegKitConfig() {
    }

    public static void enableRedirection() {
        FFmpegKitConfig.enableNativeRedirection();
    }

    public static void disableRedirection() {
        FFmpegKitConfig.disableNativeRedirection();
    }

    private static void log(long sessionId, int levelValue, byte[] logMessage) {
        LogCallback globalLogCallbackFunction;
        Level level = Level.from(levelValue);
        String text = new String(logMessage);
        Log log = new Log(sessionId, level, text);
        boolean globalCallbackDefined = false;
        boolean sessionCallbackDefined = false;
        LogRedirectionStrategy activeLogRedirectionStrategy = globalLogRedirectionStrategy;
        if (activeLogLevel == Level.AV_LOG_QUIET && levelValue != Level.AV_LOG_STDERR.getValue() || levelValue > activeLogLevel.getValue()) {
            return;
        }
        Session session = FFmpegKitConfig.getSession(sessionId);
        if (session != null) {
            activeLogRedirectionStrategy = session.getLogRedirectionStrategy();
            session.addLog(log);
            if (session.getLogCallback() != null) {
                sessionCallbackDefined = true;
                try {
                    session.getLogCallback().apply(log);
                }
                catch (Exception e) {
                    android.util.Log.e((String)TAG, (String)String.format("Exception thrown inside session log callback.%s", Exceptions.getStackTraceString((Throwable)e)));
                }
            }
        }
        if ((globalLogCallbackFunction = globalLogCallback) != null) {
            globalCallbackDefined = true;
            try {
                globalLogCallbackFunction.apply(log);
            }
            catch (Exception e) {
                android.util.Log.e((String)TAG, (String)String.format("Exception thrown inside global log callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
        switch (activeLogRedirectionStrategy) {
            case NEVER_PRINT_LOGS: {
                return;
            }
            case PRINT_LOGS_WHEN_GLOBAL_CALLBACK_NOT_DEFINED: {
                if (!globalCallbackDefined) break;
                return;
            }
            case PRINT_LOGS_WHEN_SESSION_CALLBACK_NOT_DEFINED: {
                if (!sessionCallbackDefined) break;
                return;
            }
            case PRINT_LOGS_WHEN_NO_CALLBACKS_DEFINED: {
                if (!globalCallbackDefined && !sessionCallbackDefined) break;
                return;
            }
        }
        switch (level) {
            case AV_LOG_QUIET: {
                break;
            }
            case AV_LOG_TRACE: 
            case AV_LOG_DEBUG: {
                android.util.Log.d((String)TAG, (String)text);
                break;
            }
            case AV_LOG_INFO: {
                android.util.Log.i((String)TAG, (String)text);
                break;
            }
            case AV_LOG_WARNING: {
                android.util.Log.w((String)TAG, (String)text);
                break;
            }
            case AV_LOG_ERROR: 
            case AV_LOG_FATAL: 
            case AV_LOG_PANIC: {
                android.util.Log.e((String)TAG, (String)text);
                break;
            }
            default: {
                android.util.Log.v((String)TAG, (String)text);
            }
        }
    }

    private static void statistics(long sessionId, int videoFrameNumber, float videoFps, float videoQuality, long size, double time, double bitrate, double speed) {
        StatisticsCallback globalStatisticsCallbackFunction;
        Statistics statistics = new Statistics(sessionId, videoFrameNumber, videoFps, videoQuality, size, time, bitrate, speed);
        Session session = FFmpegKitConfig.getSession(sessionId);
        if (session != null && session.isFFmpeg()) {
            FFmpegSession ffmpegSession = (FFmpegSession)session;
            ffmpegSession.addStatistics(statistics);
            if (ffmpegSession.getStatisticsCallback() != null) {
                try {
                    ffmpegSession.getStatisticsCallback().apply(statistics);
                }
                catch (Exception e) {
                    android.util.Log.e((String)TAG, (String)String.format("Exception thrown inside session statistics callback.%s", Exceptions.getStackTraceString((Throwable)e)));
                }
            }
        }
        if ((globalStatisticsCallbackFunction = globalStatisticsCallback) != null) {
            try {
                globalStatisticsCallbackFunction.apply(statistics);
            }
            catch (Exception e) {
                android.util.Log.e((String)TAG, (String)String.format("Exception thrown inside global statistics callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
    }

    public static int setFontconfigConfigurationPath(String path) {
        return FFmpegKitConfig.setNativeEnvironmentVariable("FONTCONFIG_PATH", path);
    }

    public static void setFontDirectory(Context context, String fontDirectoryPath, Map<String, String> fontNameMapping) {
        FFmpegKitConfig.setFontDirectoryList(context, Collections.singletonList(fontDirectoryPath), fontNameMapping);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setFontDirectoryList(Context context, List<String> fontDirectoryList, Map<String, String> fontNameMapping) {
        File fontConfiguration;
        File cacheDir = context.getCacheDir();
        int validFontNameMappingCount = 0;
        File tempConfigurationDirectory = new File(cacheDir, "fontconfig");
        if (!tempConfigurationDirectory.exists()) {
            boolean tempFontConfDirectoryCreated = tempConfigurationDirectory.mkdirs();
            android.util.Log.d((String)TAG, (String)String.format("Created temporary font conf directory: %s.", tempFontConfDirectoryCreated));
        }
        if ((fontConfiguration = new File(tempConfigurationDirectory, "fonts.conf")).exists()) {
            boolean fontConfigurationDeleted = fontConfiguration.delete();
            android.util.Log.d((String)TAG, (String)String.format("Deleted old temporary font configuration: %s.", fontConfigurationDeleted));
        }
        StringBuilder fontNameMappingBlock = new StringBuilder("");
        if (fontNameMapping != null && fontNameMapping.size() > 0) {
            fontNameMapping.entrySet();
            for (Map.Entry<String, String> entry : fontNameMapping.entrySet()) {
                String fontName = entry.getKey();
                String mappedFontName = entry.getValue();
                if (fontName == null || mappedFontName == null || fontName.trim().length() <= 0 || mappedFontName.trim().length() <= 0) continue;
                fontNameMappingBlock.append("    <match target=\"pattern\">\n");
                fontNameMappingBlock.append("        <test qual=\"any\" name=\"family\">\n");
                fontNameMappingBlock.append(String.format("            <string>%s</string>\n", fontName));
                fontNameMappingBlock.append("        </test>\n");
                fontNameMappingBlock.append("        <edit name=\"family\" mode=\"assign\" binding=\"same\">\n");
                fontNameMappingBlock.append(String.format("            <string>%s</string>\n", mappedFontName));
                fontNameMappingBlock.append("        </edit>\n");
                fontNameMappingBlock.append("    </match>\n");
                ++validFontNameMappingCount;
            }
        }
        StringBuilder fontConfigBuilder = new StringBuilder();
        fontConfigBuilder.append("<?xml version=\"1.0\"?>\n");
        fontConfigBuilder.append("<!DOCTYPE fontconfig SYSTEM \"fonts.dtd\">\n");
        fontConfigBuilder.append("<fontconfig>\n");
        fontConfigBuilder.append("    <dir prefix=\"cwd\">.</dir>\n");
        for (String fontDirectoryPath : fontDirectoryList) {
            fontConfigBuilder.append("    <dir>");
            fontConfigBuilder.append(fontDirectoryPath);
            fontConfigBuilder.append("</dir>\n");
        }
        fontConfigBuilder.append((CharSequence)fontNameMappingBlock);
        fontConfigBuilder.append("</fontconfig>\n");
        AtomicReference<FileOutputStream> atomicReference = new AtomicReference<FileOutputStream>();
        try {
            FileOutputStream outputStream = new FileOutputStream(fontConfiguration);
            atomicReference.set(outputStream);
            outputStream.write(fontConfigBuilder.toString().getBytes());
            outputStream.flush();
            android.util.Log.d((String)TAG, (String)String.format("Saved new temporary font configuration with %d font name mappings.", validFontNameMappingCount));
            FFmpegKitConfig.setFontconfigConfigurationPath(tempConfigurationDirectory.getAbsolutePath());
            for (String fontDirectoryPath : fontDirectoryList) {
                android.util.Log.d((String)TAG, (String)String.format("Font directory %s registered successfully.", fontDirectoryPath));
            }
        }
        catch (IOException e) {
            android.util.Log.e((String)TAG, (String)String.format("Failed to set font directory: %s.%s", Arrays.toString(fontDirectoryList.toArray()), Exceptions.getStackTraceString((Throwable)e)));
        }
        finally {
            if (atomicReference.get() != null) {
                try {
                    ((FileOutputStream)atomicReference.get()).close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public static String registerNewFFmpegPipe(Context context) {
        boolean pipesDirCreated;
        File cacheDir = context.getCacheDir();
        File pipesDir = new File(cacheDir, "pipes");
        if (!pipesDir.exists() && !(pipesDirCreated = pipesDir.mkdirs())) {
            android.util.Log.e((String)TAG, (String)String.format("Failed to create pipes directory: %s.", pipesDir.getAbsolutePath()));
            return null;
        }
        String newFFmpegPipePath = MessageFormat.format("{0}{1}{2}{3}", pipesDir, File.separator, FFMPEG_KIT_NAMED_PIPE_PREFIX, uniqueIdGenerator.getAndIncrement());
        FFmpegKitConfig.closeFFmpegPipe(newFFmpegPipePath);
        int rc = FFmpegKitConfig.registerNewNativeFFmpegPipe(newFFmpegPipePath);
        if (rc == 0) {
            return newFFmpegPipePath;
        }
        android.util.Log.e((String)TAG, (String)String.format("Failed to register new FFmpeg pipe %s. Operation failed with rc=%d.", newFFmpegPipePath, rc));
        return null;
    }

    public static void closeFFmpegPipe(String ffmpegPipePath) {
        File file = new File(ffmpegPipePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static List<String> getSupportedCameraIds(Context context) {
        ArrayList<String> detectedCameraIdList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 24) {
            detectedCameraIdList.addAll(CameraSupport.extractSupportedCameraIds(context));
        }
        return detectedCameraIdList;
    }

    public static String getFFmpegVersion() {
        return FFmpegKitConfig.getNativeFFmpegVersion();
    }

    public static String getVersion() {
        if (FFmpegKitConfig.isLTSBuild()) {
            return String.format("%s-lts", FFmpegKitConfig.getNativeVersion());
        }
        return FFmpegKitConfig.getNativeVersion();
    }

    public static boolean isLTSBuild() {
        return AbiDetect.isNativeLTSBuild();
    }

    public static String getBuildDate() {
        return FFmpegKitConfig.getNativeBuildDate();
    }

    public static void printToLogcat(int logPriority, String string) {
        int LOGGER_ENTRY_MAX_LEN = 4000;
        String remainingString = string;
        do {
            if (remainingString.length() <= 4000) {
                android.util.Log.println((int)logPriority, (String)TAG, (String)remainingString);
                remainingString = "";
                continue;
            }
            int index = remainingString.substring(0, 4000).lastIndexOf(10);
            if (index < 0) {
                android.util.Log.println((int)logPriority, (String)TAG, (String)remainingString.substring(0, 4000));
                remainingString = remainingString.substring(4000);
                continue;
            }
            android.util.Log.println((int)logPriority, (String)TAG, (String)remainingString.substring(0, index));
            remainingString = remainingString.substring(index);
        } while (remainingString.length() > 0);
    }

    public static int setEnvironmentVariable(String variableName, String variableValue) {
        return FFmpegKitConfig.setNativeEnvironmentVariable(variableName, variableValue);
    }

    public static void ignoreSignal(Signal signal) {
        FFmpegKitConfig.ignoreNativeSignal(signal.getValue());
    }

    public static void ffmpegExecute(FFmpegSession ffmpegSession) {
        ffmpegSession.startRunning();
        try {
            int returnCode = FFmpegKitConfig.nativeFFmpegExecute(ffmpegSession.getSessionId(), ffmpegSession.getArguments());
            ffmpegSession.complete(new ReturnCode(returnCode));
        }
        catch (Exception e) {
            ffmpegSession.fail(e);
            android.util.Log.w((String)TAG, (String)String.format("FFmpeg execute failed: %s.%s", FFmpegKitConfig.argumentsToString(ffmpegSession.getArguments()), Exceptions.getStackTraceString((Throwable)e)));
        }
    }

    public static void ffprobeExecute(FFprobeSession ffprobeSession) {
        ffprobeSession.startRunning();
        try {
            int returnCode = FFmpegKitConfig.nativeFFprobeExecute(ffprobeSession.getSessionId(), ffprobeSession.getArguments());
            ffprobeSession.complete(new ReturnCode(returnCode));
        }
        catch (Exception e) {
            ffprobeSession.fail(e);
            android.util.Log.w((String)TAG, (String)String.format("FFprobe execute failed: %s.%s", FFmpegKitConfig.argumentsToString(ffprobeSession.getArguments()), Exceptions.getStackTraceString((Throwable)e)));
        }
    }

    public static void getMediaInformationExecute(MediaInformationSession mediaInformationSession, int waitTimeout) {
        mediaInformationSession.startRunning();
        try {
            int returnCodeValue = FFmpegKitConfig.nativeFFprobeExecute(mediaInformationSession.getSessionId(), mediaInformationSession.getArguments());
            ReturnCode returnCode = new ReturnCode(returnCodeValue);
            mediaInformationSession.complete(returnCode);
            if (returnCode.isValueSuccess()) {
                List<Log> allLogs = mediaInformationSession.getAllLogs(waitTimeout);
                StringBuilder ffprobeJsonOutput = new StringBuilder();
                int allLogsSize = allLogs.size();
                for (int i = 0; i < allLogsSize; ++i) {
                    Log log = allLogs.get(i);
                    if (log.getLevel() != Level.AV_LOG_STDERR) continue;
                    ffprobeJsonOutput.append(log.getMessage());
                }
                MediaInformation mediaInformation = MediaInformationJsonParser.fromWithError(ffprobeJsonOutput.toString());
                mediaInformationSession.setMediaInformation(mediaInformation);
            }
        }
        catch (Exception e) {
            mediaInformationSession.fail(e);
            android.util.Log.w((String)TAG, (String)String.format("Get media information execute failed: %s.%s", FFmpegKitConfig.argumentsToString(mediaInformationSession.getArguments()), Exceptions.getStackTraceString((Throwable)e)));
        }
    }

    public static void asyncFFmpegExecute(FFmpegSession ffmpegSession) {
        AsyncFFmpegExecuteTask asyncFFmpegExecuteTask = new AsyncFFmpegExecuteTask(ffmpegSession);
        Future<?> future = asyncExecutorService.submit(asyncFFmpegExecuteTask);
        ffmpegSession.setFuture(future);
    }

    public static void asyncFFmpegExecute(FFmpegSession ffmpegSession, ExecutorService executorService) {
        AsyncFFmpegExecuteTask asyncFFmpegExecuteTask = new AsyncFFmpegExecuteTask(ffmpegSession);
        Future<?> future = executorService.submit(asyncFFmpegExecuteTask);
        ffmpegSession.setFuture(future);
    }

    public static void asyncFFprobeExecute(FFprobeSession ffprobeSession) {
        AsyncFFprobeExecuteTask asyncFFmpegExecuteTask = new AsyncFFprobeExecuteTask(ffprobeSession);
        Future<?> future = asyncExecutorService.submit(asyncFFmpegExecuteTask);
        ffprobeSession.setFuture(future);
    }

    public static void asyncFFprobeExecute(FFprobeSession ffprobeSession, ExecutorService executorService) {
        AsyncFFprobeExecuteTask asyncFFmpegExecuteTask = new AsyncFFprobeExecuteTask(ffprobeSession);
        Future<?> future = executorService.submit(asyncFFmpegExecuteTask);
        ffprobeSession.setFuture(future);
    }

    public static void asyncGetMediaInformationExecute(MediaInformationSession mediaInformationSession, int waitTimeout) {
        AsyncGetMediaInformationTask asyncGetMediaInformationTask = new AsyncGetMediaInformationTask(mediaInformationSession, waitTimeout);
        Future<?> future = asyncExecutorService.submit(asyncGetMediaInformationTask);
        mediaInformationSession.setFuture(future);
    }

    public static void asyncGetMediaInformationExecute(MediaInformationSession mediaInformationSession, ExecutorService executorService, int waitTimeout) {
        AsyncGetMediaInformationTask asyncGetMediaInformationTask = new AsyncGetMediaInformationTask(mediaInformationSession, waitTimeout);
        Future<?> future = executorService.submit(asyncGetMediaInformationTask);
        mediaInformationSession.setFuture(future);
    }

    public static int getAsyncConcurrencyLimit() {
        return asyncConcurrencyLimit;
    }

    public static void setAsyncConcurrencyLimit(int asyncConcurrencyLimit) {
        if (asyncConcurrencyLimit > 0) {
            FFmpegKitConfig.asyncConcurrencyLimit = asyncConcurrencyLimit;
            ExecutorService oldAsyncExecutorService = asyncExecutorService;
            asyncExecutorService = Executors.newFixedThreadPool(asyncConcurrencyLimit);
            oldAsyncExecutorService.shutdown();
        }
    }

    public static void enableLogCallback(LogCallback logCallback) {
        globalLogCallback = logCallback;
    }

    public static void enableStatisticsCallback(StatisticsCallback statisticsCallback) {
        globalStatisticsCallback = statisticsCallback;
    }

    public static void enableFFmpegSessionCompleteCallback(FFmpegSessionCompleteCallback ffmpegSessionCompleteCallback) {
        globalFFmpegSessionCompleteCallback = ffmpegSessionCompleteCallback;
    }

    public static FFmpegSessionCompleteCallback getFFmpegSessionCompleteCallback() {
        return globalFFmpegSessionCompleteCallback;
    }

    public static void enableFFprobeSessionCompleteCallback(FFprobeSessionCompleteCallback ffprobeSessionCompleteCallback) {
        globalFFprobeSessionCompleteCallback = ffprobeSessionCompleteCallback;
    }

    public static FFprobeSessionCompleteCallback getFFprobeSessionCompleteCallback() {
        return globalFFprobeSessionCompleteCallback;
    }

    public static void enableMediaInformationSessionCompleteCallback(MediaInformationSessionCompleteCallback mediaInformationSessionCompleteCallback) {
        globalMediaInformationSessionCompleteCallback = mediaInformationSessionCompleteCallback;
    }

    public static MediaInformationSessionCompleteCallback getMediaInformationSessionCompleteCallback() {
        return globalMediaInformationSessionCompleteCallback;
    }

    public static Level getLogLevel() {
        return activeLogLevel;
    }

    public static void setLogLevel(Level level) {
        if (level != null) {
            activeLogLevel = level;
            FFmpegKitConfig.setNativeLogLevel(level.getValue());
        }
    }

    static String extractExtensionFromSafDisplayName(String safDisplayName) {
        String rawExtension = safDisplayName;
        if (safDisplayName.lastIndexOf(".") >= 0) {
            rawExtension = safDisplayName.substring(safDisplayName.lastIndexOf("."));
        }
        try {
            return new StringTokenizer(rawExtension, " .").nextToken();
        }
        catch (Exception e) {
            android.util.Log.w((String)TAG, (String)String.format("Failed to extract extension from saf display name: %s.%s", safDisplayName, Exceptions.getStackTraceString((Throwable)e)));
            return "raw";
        }
    }

    public static String getSafParameter(Context context, Uri uri, String openMode) {
        if (Build.VERSION.SDK_INT < 19) {
            android.util.Log.i((String)TAG, (String)String.format("getSafParameter is not supported on API Level %d", Build.VERSION.SDK_INT));
            return "";
        }
        String displayName = "unknown";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);){
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
            }
        }
        catch (Throwable t) {
            android.util.Log.e((String)TAG, (String)String.format("Failed to get %s column for %s.%s", "_display_name", uri.toString(), Exceptions.getStackTraceString((Throwable)t)));
            throw t;
        }
        int safId = uniqueIdGenerator.getAndIncrement();
        safIdMap.put(safId, new SAFProtocolUrl(safId, uri, openMode, context.getContentResolver()));
        return "saf:" + safId + "." + FFmpegKitConfig.extractExtensionFromSafDisplayName(displayName);
    }

    public static String getSafParameterForRead(Context context, Uri uri) {
        return FFmpegKitConfig.getSafParameter(context, uri, "r");
    }

    public static String getSafParameterForWrite(Context context, Uri uri) {
        return FFmpegKitConfig.getSafParameter(context, uri, "w");
    }

    private static int safOpen(int safId) {
        try {
            SAFProtocolUrl safUrl = (SAFProtocolUrl)safIdMap.get(safId);
            if (safUrl != null) {
                ParcelFileDescriptor parcelFileDescriptor = safUrl.getContentResolver().openFileDescriptor(safUrl.getUri(), safUrl.getOpenMode());
                safUrl.setParcelFileDescriptor(parcelFileDescriptor);
                int fd = parcelFileDescriptor.getFd();
                safFileDescriptorMap.put(fd, safUrl);
                return fd;
            }
            android.util.Log.e((String)TAG, (String)String.format("SAF id %d not found.", safId));
        }
        catch (Throwable t) {
            android.util.Log.e((String)TAG, (String)String.format("Failed to open SAF id: %d.%s", safId, Exceptions.getStackTraceString((Throwable)t)));
        }
        return 0;
    }

    private static int safClose(int fileDescriptor) {
        try {
            SAFProtocolUrl safProtocolUrl = (SAFProtocolUrl)safFileDescriptorMap.get(fileDescriptor);
            if (safProtocolUrl != null) {
                ParcelFileDescriptor parcelFileDescriptor = safProtocolUrl.getParcelFileDescriptor();
                if (parcelFileDescriptor != null) {
                    safFileDescriptorMap.delete(fileDescriptor);
                    safIdMap.delete(safProtocolUrl.getSafId().intValue());
                    parcelFileDescriptor.close();
                    return 1;
                }
                android.util.Log.e((String)TAG, (String)String.format("ParcelFileDescriptor for SAF fd %d not found.", fileDescriptor));
            } else {
                android.util.Log.e((String)TAG, (String)String.format("SAF fd %d not found.", fileDescriptor));
            }
        }
        catch (Throwable t) {
            android.util.Log.e((String)TAG, (String)String.format("Failed to close SAF fd: %d.%s", fileDescriptor, Exceptions.getStackTraceString((Throwable)t)));
        }
        return 0;
    }

    public static int getSessionHistorySize() {
        return sessionHistorySize;
    }

    public static void setSessionHistorySize(int sessionHistorySize) {
        if (sessionHistorySize >= 1000) {
            throw new IllegalArgumentException("Session history size must not exceed the hard limit!");
        }
        if (sessionHistorySize > 0) {
            FFmpegKitConfig.sessionHistorySize = sessionHistorySize;
            FFmpegKitConfig.deleteExpiredSessions();
        }
    }

    private static void deleteExpiredSessions() {
        while (sessionHistoryList.size() > sessionHistorySize) {
            try {
                Session expiredSession = sessionHistoryList.remove(0);
                if (expiredSession == null) continue;
                sessionHistoryMap.remove(expiredSession.getSessionId());
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void addSession(Session session) {
        Object object = sessionHistoryLock;
        synchronized (object) {
            boolean sessionAlreadyAdded = sessionHistoryMap.containsKey(session.getSessionId());
            if (!sessionAlreadyAdded) {
                sessionHistoryMap.put(session.getSessionId(), session);
                sessionHistoryList.add(session);
                FFmpegKitConfig.deleteExpiredSessions();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Session getSession(long sessionId) {
        Object object = sessionHistoryLock;
        synchronized (object) {
            return sessionHistoryMap.get(sessionId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Session getLastSession() {
        Object object = sessionHistoryLock;
        synchronized (object) {
            if (sessionHistoryList.size() > 0) {
                return sessionHistoryList.get(sessionHistoryList.size() - 1);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Session getLastCompletedSession() {
        Object object = sessionHistoryLock;
        synchronized (object) {
            for (int i = sessionHistoryList.size() - 1; i >= 0; --i) {
                Session session = sessionHistoryList.get(i);
                if (session.getState() != SessionState.COMPLETED) continue;
                return session;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<Session> getSessions() {
        Object object = sessionHistoryLock;
        synchronized (object) {
            return new LinkedList<Session>(sessionHistoryList);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearSessions() {
        Object object = sessionHistoryLock;
        synchronized (object) {
            sessionHistoryList.clear();
            sessionHistoryMap.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<FFmpegSession> getFFmpegSessions() {
        LinkedList<FFmpegSession> list = new LinkedList<FFmpegSession>();
        Object object = sessionHistoryLock;
        synchronized (object) {
            for (Session session : sessionHistoryList) {
                if (!session.isFFmpeg()) continue;
                list.add((FFmpegSession)session);
            }
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<FFprobeSession> getFFprobeSessions() {
        LinkedList<FFprobeSession> list = new LinkedList<FFprobeSession>();
        Object object = sessionHistoryLock;
        synchronized (object) {
            for (Session session : sessionHistoryList) {
                if (!session.isFFprobe()) continue;
                list.add((FFprobeSession)session);
            }
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<MediaInformationSession> getMediaInformationSessions() {
        LinkedList<MediaInformationSession> list = new LinkedList<MediaInformationSession>();
        Object object = sessionHistoryLock;
        synchronized (object) {
            for (Session session : sessionHistoryList) {
                if (!session.isMediaInformation()) continue;
                list.add((MediaInformationSession)session);
            }
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<Session> getSessionsByState(SessionState state) {
        LinkedList<Session> list = new LinkedList<Session>();
        Object object = sessionHistoryLock;
        synchronized (object) {
            for (Session session : sessionHistoryList) {
                if (session.getState() != state) continue;
                list.add(session);
            }
        }
        return list;
    }

    public static LogRedirectionStrategy getLogRedirectionStrategy() {
        return globalLogRedirectionStrategy;
    }

    public static void setLogRedirectionStrategy(LogRedirectionStrategy logRedirectionStrategy) {
        globalLogRedirectionStrategy = logRedirectionStrategy;
    }

    public static String sessionStateToString(SessionState state) {
        return state.toString();
    }

    public static String[] parseArguments(String command) {
        ArrayList<String> argumentList = new ArrayList<String>();
        StringBuilder currentArgument = new StringBuilder();
        boolean singleQuoteStarted = false;
        boolean doubleQuoteStarted = false;
        for (int i = 0; i < command.length(); ++i) {
            Character previousChar = i > 0 ? Character.valueOf(command.charAt(i - 1)) : null;
            char currentChar = command.charAt(i);
            if (currentChar == ' ') {
                if (singleQuoteStarted || doubleQuoteStarted) {
                    currentArgument.append(currentChar);
                    continue;
                }
                if (currentArgument.length() <= 0) continue;
                argumentList.add(currentArgument.toString());
                currentArgument = new StringBuilder();
                continue;
            }
            if (currentChar == '\'' && (previousChar == null || previousChar.charValue() != '\\')) {
                if (singleQuoteStarted) {
                    singleQuoteStarted = false;
                    continue;
                }
                if (doubleQuoteStarted) {
                    currentArgument.append(currentChar);
                    continue;
                }
                singleQuoteStarted = true;
                continue;
            }
            if (currentChar == '\"' && (previousChar == null || previousChar.charValue() != '\\')) {
                if (doubleQuoteStarted) {
                    doubleQuoteStarted = false;
                    continue;
                }
                if (singleQuoteStarted) {
                    currentArgument.append(currentChar);
                    continue;
                }
                doubleQuoteStarted = true;
                continue;
            }
            currentArgument.append(currentChar);
        }
        if (currentArgument.length() > 0) {
            argumentList.add(currentArgument.toString());
        }
        return argumentList.toArray(new String[0]);
    }

    public static String argumentsToString(String[] arguments) {
        if (arguments == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arguments.length; ++i) {
            if (i > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(arguments[i]);
        }
        return stringBuilder.toString();
    }

    private static native void enableNativeRedirection();

    private static native void disableNativeRedirection();

    static native int getNativeLogLevel();

    private static native void setNativeLogLevel(int var0);

    private static native String getNativeFFmpegVersion();

    private static native String getNativeVersion();

    private static native int nativeFFmpegExecute(long var0, String[] var2);

    static native int nativeFFprobeExecute(long var0, String[] var2);

    static native void nativeFFmpegCancel(long var0);

    public static native int messagesInTransmit(long var0);

    private static native int registerNewNativeFFmpegPipe(String var0);

    private static native String getNativeBuildDate();

    private static native int setNativeEnvironmentVariable(String var0, String var1);

    private static native void ignoreNativeSignal(int var0);

    static {
        Exceptions.registerRootPackage((String)"com.arthenica");
        android.util.Log.i((String)TAG, (String)"Loading ffmpeg-kit.");
        boolean nativeFFmpegTriedAndFailed = NativeLoader.loadFFmpeg();
        Abi.class.getName();
        FFmpegKit.class.getName();
        FFprobeKit.class.getName();
        NativeLoader.loadFFmpegKit(nativeFFmpegTriedAndFailed);
        uniqueIdGenerator = new AtomicInteger(1);
        activeLogLevel = Level.from(NativeLoader.loadLogLevel());
        asyncConcurrencyLimit = 10;
        asyncExecutorService = Executors.newFixedThreadPool(asyncConcurrencyLimit);
        sessionHistorySize = 10;
        sessionHistoryMap = new LinkedHashMap<Long, Session>(){

            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Session> eldest) {
                return this.size() > sessionHistorySize;
            }
        };
        sessionHistoryList = new LinkedList<Session>();
        sessionHistoryLock = new Object();
        globalLogCallback = null;
        globalStatisticsCallback = null;
        globalFFmpegSessionCompleteCallback = null;
        globalFFprobeSessionCompleteCallback = null;
        globalMediaInformationSessionCompleteCallback = null;
        safIdMap = new SparseArray();
        safFileDescriptorMap = new SparseArray();
        globalLogRedirectionStrategy = LogRedirectionStrategy.PRINT_LOGS_WHEN_NO_CALLBACKS_DEFINED;
        android.util.Log.i((String)TAG, (String)String.format("Loaded ffmpeg-kit-%s-%s-%s-%s.", NativeLoader.loadPackageName(), NativeLoader.loadAbi(), NativeLoader.loadVersion(), NativeLoader.loadBuildDate()));
    }

    static class SAFProtocolUrl {
        private final Integer safId;
        private final Uri uri;
        private final String openMode;
        private final ContentResolver contentResolver;
        private ParcelFileDescriptor parcelFileDescriptor;

        public SAFProtocolUrl(Integer safId, Uri uri, String openMode, ContentResolver contentResolver) {
            this.safId = safId;
            this.uri = uri;
            this.openMode = openMode;
            this.contentResolver = contentResolver;
        }

        public Integer getSafId() {
            return this.safId;
        }

        public Uri getUri() {
            return this.uri;
        }

        public String getOpenMode() {
            return this.openMode;
        }

        public ContentResolver getContentResolver() {
            return this.contentResolver;
        }

        public void setParcelFileDescriptor(ParcelFileDescriptor parcelFileDescriptor) {
            this.parcelFileDescriptor = parcelFileDescriptor;
        }

        public ParcelFileDescriptor getParcelFileDescriptor() {
            return this.parcelFileDescriptor;
        }
    }
}

