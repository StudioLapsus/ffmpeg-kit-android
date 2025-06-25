/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  com.arthenica.smartexception.java.Exceptions
 */
package com.arthenica.ffmpegkit;

import android.util.Log;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.smartexception.java.Exceptions;

public class AsyncFFmpegExecuteTask
implements Runnable {
    private final FFmpegSession ffmpegSession;
    private final FFmpegSessionCompleteCallback completeCallback;

    public AsyncFFmpegExecuteTask(FFmpegSession ffmpegSession) {
        this.ffmpegSession = ffmpegSession;
        this.completeCallback = ffmpegSession.getCompleteCallback();
    }

    @Override
    public void run() {
        FFmpegSessionCompleteCallback globalFFmpegSessionCompleteCallback;
        FFmpegKitConfig.ffmpegExecute(this.ffmpegSession);
        if (this.completeCallback != null) {
            try {
                this.completeCallback.apply(this.ffmpegSession);
            }
            catch (Exception e) {
                Log.e((String)"ffmpeg-kit", (String)String.format("Exception thrown inside session complete callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
        if ((globalFFmpegSessionCompleteCallback = FFmpegKitConfig.getFFmpegSessionCompleteCallback()) != null) {
            try {
                globalFFmpegSessionCompleteCallback.apply(this.ffmpegSession);
            }
            catch (Exception e) {
                Log.e((String)"ffmpeg-kit", (String)String.format("Exception thrown inside global complete callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
    }
}

