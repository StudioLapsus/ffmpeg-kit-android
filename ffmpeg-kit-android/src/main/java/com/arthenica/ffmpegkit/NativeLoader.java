/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.util.Log
 *  com.arthenica.smartexception.java.Exceptions
 */
package com.arthenica.ffmpegkit;

import android.os.Build;
import android.util.Log;
import com.arthenica.ffmpegkit.Abi;
import com.arthenica.ffmpegkit.AbiDetect;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.Level;
import com.arthenica.ffmpegkit.Packages;
import com.arthenica.smartexception.java.Exceptions;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NativeLoader {
    static final String[] FFMPEG_LIBRARIES = new String[]{"avutil", "swscale", "swresample", "avcodec", "avformat", "avfilter", "avdevice"};
    static final String[] LIBRARIES_LINKED_WITH_CXX = new String[]{"chromaprint", "openh264", "rubberband", "snappy", "srt", "tesseract", "x265", "zimg", "libilbc"};

    static boolean isTestModeDisabled() {
        return System.getProperty("enable.ffmpeg.kit.test.mode") == null;
    }

    private static void loadLibrary(String libraryName) {
        if (NativeLoader.isTestModeDisabled()) {
            try {
                System.loadLibrary(libraryName);
            }
            catch (UnsatisfiedLinkError e) {
                throw new Error(String.format("FFmpegKit failed to start on %s.", NativeLoader.getDeviceDebugInformation()), e);
            }
        }
    }

    private static List<String> loadExternalLibraries() {
        if (NativeLoader.isTestModeDisabled()) {
            return Packages.getExternalLibraries();
        }
        return Collections.emptyList();
    }

    private static String loadNativeAbi() {
        if (NativeLoader.isTestModeDisabled()) {
            return AbiDetect.getNativeAbi();
        }
        return Abi.ABI_X86_64.getName();
    }

    static String loadAbi() {
        if (NativeLoader.isTestModeDisabled()) {
            return AbiDetect.getAbi();
        }
        return Abi.ABI_X86_64.getName();
    }

    static String loadPackageName() {
        if (NativeLoader.isTestModeDisabled()) {
            return Packages.getPackageName();
        }
        return "test";
    }

    static String loadVersion() {
        String version = "6.0";
        if (NativeLoader.isTestModeDisabled()) {
            return FFmpegKitConfig.getVersion();
        }
        if (NativeLoader.loadIsLTSBuild()) {
            return String.format("%s-lts", "6.0");
        }
        return "6.0";
    }

    static boolean loadIsLTSBuild() {
        if (NativeLoader.isTestModeDisabled()) {
            return AbiDetect.isNativeLTSBuild();
        }
        return true;
    }

    static int loadLogLevel() {
        if (NativeLoader.isTestModeDisabled()) {
            return FFmpegKitConfig.getNativeLogLevel();
        }
        return Level.AV_LOG_DEBUG.getValue();
    }

    static String loadBuildDate() {
        if (NativeLoader.isTestModeDisabled()) {
            return FFmpegKitConfig.getBuildDate();
        }
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    static void enableRedirection() {
        if (NativeLoader.isTestModeDisabled()) {
            FFmpegKitConfig.enableRedirection();
        }
    }

    static void loadFFmpegKitAbiDetect() {
        NativeLoader.loadLibrary("ffmpegkit_abidetect");
    }

    static boolean loadFFmpeg() {
        boolean nativeFFmpegLoaded = false;
        boolean nativeFFmpegTriedAndFailed = false;
        if (Build.VERSION.SDK_INT < 21) {
            List<String> externalLibrariesEnabled = NativeLoader.loadExternalLibraries();
            for (String dependantLibrary : LIBRARIES_LINKED_WITH_CXX) {
                if (!externalLibrariesEnabled.contains(dependantLibrary)) continue;
                NativeLoader.loadLibrary("c++_shared");
                break;
            }
            if ("arm-v7a".equals(NativeLoader.loadNativeAbi())) {
                try {
                    for (String ffmpegLibrary : FFMPEG_LIBRARIES) {
                        NativeLoader.loadLibrary(ffmpegLibrary + "_neon");
                    }
                    nativeFFmpegLoaded = true;
                }
                catch (Error e) {
                    Log.i((String)"ffmpeg-kit", (String)String.format("NEON supported armeabi-v7a ffmpeg library not found. Loading default armeabi-v7a library.%s", Exceptions.getStackTraceString((Throwable)e)));
                    nativeFFmpegTriedAndFailed = true;
                }
            }
            if (!nativeFFmpegLoaded) {
                for (String ffmpegLibrary : FFMPEG_LIBRARIES) {
                    NativeLoader.loadLibrary(ffmpegLibrary);
                }
            }
        }
        return nativeFFmpegTriedAndFailed;
    }

    static void loadFFmpegKit(boolean nativeFFmpegTriedAndFailed) {
        boolean nativeFFmpegKitLoaded = false;
        if (!nativeFFmpegTriedAndFailed && "arm-v7a".equals(NativeLoader.loadNativeAbi())) {
            try {
                NativeLoader.loadLibrary("ffmpegkit_armv7a_neon");
                nativeFFmpegKitLoaded = true;
                AbiDetect.setArmV7aNeonLoaded();
            }
            catch (Error e) {
                Log.i((String)"ffmpeg-kit", (String)String.format("NEON supported armeabi-v7a ffmpegkit library not found. Loading default armeabi-v7a library.%s", Exceptions.getStackTraceString((Throwable)e)));
            }
        }
        if (!nativeFFmpegKitLoaded) {
            NativeLoader.loadLibrary("ffmpegkit");
        }
    }

    static String getDeviceDebugInformation() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("brand: ");
        stringBuilder.append(Build.BRAND);
        stringBuilder.append(", model: ");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append(", device: ");
        stringBuilder.append(Build.DEVICE);
        stringBuilder.append(", api level: ");
        stringBuilder.append(Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 21) {
            stringBuilder.append(", abis: ");
            stringBuilder.append(FFmpegKitConfig.argumentsToString(Build.SUPPORTED_ABIS));
            stringBuilder.append(", 32bit abis: ");
            stringBuilder.append(FFmpegKitConfig.argumentsToString(Build.SUPPORTED_32_BIT_ABIS));
            stringBuilder.append(", 64bit abis: ");
            stringBuilder.append(FFmpegKitConfig.argumentsToString(Build.SUPPORTED_64_BIT_ABIS));
        } else {
            stringBuilder.append(", cpu abis: ");
            stringBuilder.append(Build.CPU_ABI);
            stringBuilder.append(", cpu abi2s: ");
            stringBuilder.append(Build.CPU_ABI2);
        }
        return stringBuilder.toString();
    }
}

