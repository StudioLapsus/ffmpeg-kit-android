/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.NativeLoader;

public class AbiDetect {
    static final String ARM_V7A = "arm-v7a";
    static final String ARM_V7A_NEON = "arm-v7a-neon";
    private static boolean armV7aNeonLoaded = false;

    private AbiDetect() {
    }

    static void setArmV7aNeonLoaded() {
        armV7aNeonLoaded = true;
    }

    public static String getAbi() {
        if (armV7aNeonLoaded) {
            return ARM_V7A_NEON;
        }
        return AbiDetect.getNativeAbi();
    }

    public static String getCpuAbi() {
        return AbiDetect.getNativeCpuAbi();
    }

    static native String getNativeAbi();

    static native String getNativeCpuAbi();

    static native boolean isNativeLTSBuild();

    static native String getNativeBuildConf();

    static {
        NativeLoader.loadFFmpegKitAbiDetect();
        FFmpegKit.class.getName();
        FFmpegKitConfig.class.getName();
        FFprobeKit.class.getName();
    }
}

