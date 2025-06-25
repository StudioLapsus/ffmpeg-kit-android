/*
 * Decompiled with CFR 0.152.
 */
package com.arthenica.ffmpegkit;

public class ReturnCode {
    public static int SUCCESS = 0;
    public static int CANCEL = 255;
    private final int value;

    public ReturnCode(int value) {
        this.value = value;
    }

    public static boolean isSuccess(ReturnCode returnCode) {
        return returnCode != null && returnCode.getValue() == SUCCESS;
    }

    public static boolean isCancel(ReturnCode returnCode) {
        return returnCode != null && returnCode.getValue() == CANCEL;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isValueSuccess() {
        return this.value == SUCCESS;
    }

    public boolean isValueError() {
        return this.value != SUCCESS && this.value != CANCEL;
    }

    public boolean isValueCancel() {
        return this.value == CANCEL;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

