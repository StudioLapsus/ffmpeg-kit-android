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
import com.arthenica.ffmpegkit.FFprobeSession;
import com.arthenica.ffmpegkit.FFprobeSessionCompleteCallback;
import com.arthenica.smartexception.java.Exceptions;

public class AsyncFFprobeExecuteTask
implements Runnable {
    private final FFprobeSession ffprobeSession;
    private final FFprobeSessionCompleteCallback completeCallback;

    public AsyncFFprobeExecuteTask(FFprobeSession ffprobeSession) {
        this.ffprobeSession = ffprobeSession;
        this.completeCallback = ffprobeSession.getCompleteCallback();
    }

    @Override
    public void run() {
        FFprobeSessionCompleteCallback globalFFprobeSessionCompleteCallback;
        FFmpegKitConfig.ffprobeExecute(this.ffprobeSession);
        if (this.completeCallback != null) {
            try {
                this.completeCallback.apply(this.ffprobeSession);
            }
            catch (Exception e) {
                Log.e((String)"ffmpeg-kit", (String)String.format("Exception thrown inside session complete callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
        if ((globalFFprobeSessionCompleteCallback = FFmpegKitConfig.getFFprobeSessionCompleteCallback()) != null) {
            try {
                globalFFprobeSessionCompleteCallback.apply(this.ffprobeSession);
            }
            catch (Exception e) {
                Log.e((String)"ffmpeg-kit", (String)String.format("Exception thrown inside global complete callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
    }
}

