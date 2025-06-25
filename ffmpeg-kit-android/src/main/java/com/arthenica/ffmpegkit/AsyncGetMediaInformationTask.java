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
import com.arthenica.ffmpegkit.MediaInformationSession;
import com.arthenica.ffmpegkit.MediaInformationSessionCompleteCallback;
import com.arthenica.smartexception.java.Exceptions;

public class AsyncGetMediaInformationTask
implements Runnable {
    private final MediaInformationSession mediaInformationSession;
    private final MediaInformationSessionCompleteCallback completeCallback;
    private final Integer waitTimeout;

    public AsyncGetMediaInformationTask(MediaInformationSession mediaInformationSession) {
        this(mediaInformationSession, 5000);
    }

    public AsyncGetMediaInformationTask(MediaInformationSession mediaInformationSession, Integer waitTimeout) {
        this.mediaInformationSession = mediaInformationSession;
        this.completeCallback = mediaInformationSession.getCompleteCallback();
        this.waitTimeout = waitTimeout;
    }

    @Override
    public void run() {
        MediaInformationSessionCompleteCallback globalMediaInformationSessionCompleteCallback;
        FFmpegKitConfig.getMediaInformationExecute(this.mediaInformationSession, this.waitTimeout);
        if (this.completeCallback != null) {
            try {
                this.completeCallback.apply(this.mediaInformationSession);
            }
            catch (Exception e) {
                Log.e((String)"ffmpeg-kit", (String)String.format("Exception thrown inside session complete callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
        if ((globalMediaInformationSessionCompleteCallback = FFmpegKitConfig.getMediaInformationSessionCompleteCallback()) != null) {
            try {
                globalMediaInformationSessionCompleteCallback.apply(this.mediaInformationSession);
            }
            catch (Exception e) {
                Log.e((String)"ffmpeg-kit", (String)String.format("Exception thrown inside global complete callback.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
    }
}

