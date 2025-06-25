/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  com.arthenica.smartexception.java.Exceptions
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.Log;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.LogRedirectionStrategy;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.Session;
import com.arthenica.ffmpegkit.SessionState;
import com.arthenica.smartexception.java.Exceptions;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractSession
implements Session {
    protected static final AtomicLong sessionIdGenerator = new AtomicLong(1L);
    public static final int DEFAULT_TIMEOUT_FOR_ASYNCHRONOUS_MESSAGES_IN_TRANSMIT = 5000;
    protected final long sessionId = sessionIdGenerator.getAndIncrement();
    protected final LogCallback logCallback;
    protected final Date createTime;
    protected Date startTime;
    protected Date endTime;
    protected final String[] arguments;
    protected final List<Log> logs;
    protected final Object logsLock;
    protected Future<?> future;
    protected SessionState state;
    protected ReturnCode returnCode;
    protected String failStackTrace;
    protected final LogRedirectionStrategy logRedirectionStrategy;

    protected AbstractSession(String[] arguments, LogCallback logCallback, LogRedirectionStrategy logRedirectionStrategy) {
        this.logCallback = logCallback;
        this.createTime = new Date();
        this.startTime = null;
        this.endTime = null;
        this.arguments = arguments;
        this.logs = new LinkedList<Log>();
        this.logsLock = new Object();
        this.future = null;
        this.state = SessionState.CREATED;
        this.returnCode = null;
        this.failStackTrace = null;
        this.logRedirectionStrategy = logRedirectionStrategy;
        FFmpegKitConfig.addSession(this);
    }

    @Override
    public LogCallback getLogCallback() {
        return this.logCallback;
    }

    @Override
    public long getSessionId() {
        return this.sessionId;
    }

    @Override
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override
    public Date getStartTime() {
        return this.startTime;
    }

    @Override
    public Date getEndTime() {
        return this.endTime;
    }

    @Override
    public long getDuration() {
        Date startTime = this.startTime;
        Date endTime = this.endTime;
        if (startTime != null && endTime != null) {
            return endTime.getTime() - startTime.getTime();
        }
        return 0L;
    }

    @Override
    public String[] getArguments() {
        return this.arguments;
    }

    @Override
    public String getCommand() {
        return FFmpegKitConfig.argumentsToString(this.arguments);
    }

    @Override
    public List<Log> getAllLogs(int waitTimeout) {
        this.waitForAsynchronousMessagesInTransmit(waitTimeout);
        if (this.thereAreAsynchronousMessagesInTransmit()) {
            android.util.Log.i((String)"ffmpeg-kit", (String)String.format("getAllLogs was called to return all logs but there are still logs being transmitted for session id %d.", this.sessionId));
        }
        return this.getLogs();
    }

    @Override
    public List<Log> getAllLogs() {
        return this.getAllLogs(5000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Log> getLogs() {
        Object object = this.logsLock;
        synchronized (object) {
            return new LinkedList<Log>(this.logs);
        }
    }

    @Override
    public String getAllLogsAsString(int waitTimeout) {
        this.waitForAsynchronousMessagesInTransmit(waitTimeout);
        if (this.thereAreAsynchronousMessagesInTransmit()) {
            android.util.Log.i((String)"ffmpeg-kit", (String)String.format("getAllLogsAsString was called to return all logs but there are still logs being transmitted for session id %d.", this.sessionId));
        }
        return this.getLogsAsString();
    }

    @Override
    public String getAllLogsAsString() {
        return this.getAllLogsAsString(5000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getLogsAsString() {
        StringBuilder concatenatedString = new StringBuilder();
        Object object = this.logsLock;
        synchronized (object) {
            for (Log log : this.logs) {
                concatenatedString.append(log.getMessage());
            }
        }
        return concatenatedString.toString();
    }

    @Override
    public String getOutput() {
        return this.getAllLogsAsString();
    }

    @Override
    public SessionState getState() {
        return this.state;
    }

    @Override
    public ReturnCode getReturnCode() {
        return this.returnCode;
    }

    @Override
    public String getFailStackTrace() {
        return this.failStackTrace;
    }

    @Override
    public LogRedirectionStrategy getLogRedirectionStrategy() {
        return this.logRedirectionStrategy;
    }

    @Override
    public boolean thereAreAsynchronousMessagesInTransmit() {
        return FFmpegKitConfig.messagesInTransmit(this.sessionId) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLog(Log log) {
        Object object = this.logsLock;
        synchronized (object) {
            this.logs.add(log);
        }
    }

    @Override
    public Future<?> getFuture() {
        return this.future;
    }

    @Override
    public void cancel() {
        if (this.state == SessionState.RUNNING) {
            FFmpegKit.cancel(this.sessionId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void waitForAsynchronousMessagesInTransmit(int timeout) {
        long start = System.currentTimeMillis();
        while (this.thereAreAsynchronousMessagesInTransmit() && System.currentTimeMillis() < start + (long)timeout) {
            AbstractSession abstractSession = this;
            synchronized (abstractSession) {
                try {
                    this.wait(100L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
    }

    void setFuture(Future<?> future) {
        this.future = future;
    }

    void startRunning() {
        this.state = SessionState.RUNNING;
        this.startTime = new Date();
    }

    void complete(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.state = SessionState.COMPLETED;
        this.endTime = new Date();
    }

    void fail(Exception exception) {
        this.failStackTrace = Exceptions.getStackTraceString((Throwable)exception);
        this.state = SessionState.FAILED;
        this.endTime = new Date();
    }
}

