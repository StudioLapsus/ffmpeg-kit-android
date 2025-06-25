/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.hardware.camera2.CameraAccessException
 *  android.hardware.camera2.CameraCharacteristics
 *  android.hardware.camera2.CameraManager
 *  android.os.Build$VERSION
 *  android.util.Log
 */
package com.arthenica.ffmpegkit;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

class CameraSupport {
    CameraSupport() {
    }

    static List<String> extractSupportedCameraIds(Context context) {
        ArrayList<String> detectedCameraIdList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                CameraManager manager = (CameraManager)context.getSystemService("camera");
                if (manager != null) {
                    String[] cameraIdList;
                    for (String cameraId : cameraIdList = manager.getCameraIdList()) {
                        CameraCharacteristics chars = manager.getCameraCharacteristics(cameraId);
                        Integer cameraSupport = (Integer)chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                        if (cameraSupport != null && cameraSupport == 2) {
                            Log.d((String)"ffmpeg-kit", (String)("Detected camera with id " + cameraId + " has LEGACY hardware level which is not supported by Android Camera2 NDK API."));
                            continue;
                        }
                        if (cameraSupport == null) continue;
                        detectedCameraIdList.add(cameraId);
                    }
                }
            }
            catch (CameraAccessException e) {
                Log.w((String)"ffmpeg-kit", (String)"Detecting camera ids failed.", (Throwable)e);
            }
        }
        return detectedCameraIdList;
    }
}

