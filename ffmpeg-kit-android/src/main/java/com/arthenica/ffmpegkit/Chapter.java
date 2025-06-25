/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.arthenica.ffmpegkit;

import org.json.JSONObject;

public class Chapter {
    public static final String KEY_ID = "id";
    public static final String KEY_TIME_BASE = "time_base";
    public static final String KEY_START = "start";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_END = "end";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_TAGS = "tags";
    private final JSONObject jsonObject;

    public Chapter(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Long getId() {
        return this.getNumberProperty(KEY_ID);
    }

    public String getTimeBase() {
        return this.getStringProperty(KEY_TIME_BASE);
    }

    public Long getStart() {
        return this.getNumberProperty(KEY_START);
    }

    public String getStartTime() {
        return this.getStringProperty(KEY_START_TIME);
    }

    public Long getEnd() {
        return this.getNumberProperty(KEY_END);
    }

    public String getEndTime() {
        return this.getStringProperty(KEY_END_TIME);
    }

    public JSONObject getTags() {
        return this.getProperty(KEY_TAGS);
    }

    public String getStringProperty(String key) {
        JSONObject allProperties = this.getAllProperties();
        if (allProperties == null) {
            return null;
        }
        if (allProperties.has(key)) {
            return allProperties.optString(key);
        }
        return null;
    }

    public Long getNumberProperty(String key) {
        JSONObject allProperties = this.getAllProperties();
        if (allProperties == null) {
            return null;
        }
        if (allProperties.has(key)) {
            return allProperties.optLong(key);
        }
        return null;
    }

    public JSONObject getProperty(String key) {
        JSONObject allProperties = this.getAllProperties();
        if (allProperties == null) {
            return null;
        }
        return allProperties.optJSONObject(key);
    }

    public JSONObject getAllProperties() {
        return this.jsonObject;
    }
}

