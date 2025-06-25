/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.Log;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.LogRedirectionStrategy;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.SessionState;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public interface Session {
    public LogCallback getLogCallback();

    public long getSessionId();

    public Date getCreateTime();

    public Date getStartTime();

    public Date getEndTime();

    public long getDuration();

    public String[] getArguments();

    public String getCommand();

    public List<Log> getAllLogs(int var1);

    public List<Log> getAllLogs();

    public List<Log> getLogs();

    public String getAllLogsAsString(int var1);

    public String getAllLogsAsString();

    public String getLogsAsString();

    public String getOutput();

    public SessionState getState();

    public ReturnCode getReturnCode();

    public String getFailStackTrace();

    public LogRedirectionStrategy getLogRedirectionStrategy();

    public boolean thereAreAsynchronousMessagesInTransmit();

    public void addLog(Log var1);

    public Future<?> getFuture();

    public boolean isFFmpeg();

    public boolean isFFprobe();

    public boolean isMediaInformation();

    public void cancel();
}

