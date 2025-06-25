/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.arthenica.ffmpegkit;

import org.json.JSONObject;

public class StreamInformation {
    public static final String KEY_INDEX = "index";
    public static final String KEY_TYPE = "codec_type";
    public static final String KEY_CODEC = "codec_name";
    public static final String KEY_CODEC_LONG = "codec_long_name";
    public static final String KEY_FORMAT = "pix_fmt";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_BIT_RATE = "bit_rate";
    public static final String KEY_SAMPLE_RATE = "sample_rate";
    public static final String KEY_SAMPLE_FORMAT = "sample_fmt";
    public static final String KEY_CHANNEL_LAYOUT = "channel_layout";
    public static final String KEY_SAMPLE_ASPECT_RATIO = "sample_aspect_ratio";
    public static final String KEY_DISPLAY_ASPECT_RATIO = "display_aspect_ratio";
    public static final String KEY_AVERAGE_FRAME_RATE = "avg_frame_rate";
    public static final String KEY_REAL_FRAME_RATE = "r_frame_rate";
    public static final String KEY_TIME_BASE = "time_base";
    public static final String KEY_CODEC_TIME_BASE = "codec_time_base";
    public static final String KEY_TAGS = "tags";
    private final JSONObject jsonObject;

    public StreamInformation(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Long getIndex() {
        return this.getNumberProperty(KEY_INDEX);
    }

    public String getType() {
        return this.getStringProperty(KEY_TYPE);
    }

    public String getCodec() {
        return this.getStringProperty(KEY_CODEC);
    }

    public String getCodecLong() {
        return this.getStringProperty(KEY_CODEC_LONG);
    }

    public String getFormat() {
        return this.getStringProperty(KEY_FORMAT);
    }

    public Long getWidth() {
        return this.getNumberProperty(KEY_WIDTH);
    }

    public Long getHeight() {
        return this.getNumberProperty(KEY_HEIGHT);
    }

    public String getBitrate() {
        return this.getStringProperty(KEY_BIT_RATE);
    }

    public String getSampleRate() {
        return this.getStringProperty(KEY_SAMPLE_RATE);
    }

    public String getSampleFormat() {
        return this.getStringProperty(KEY_SAMPLE_FORMAT);
    }

    public String getChannelLayout() {
        return this.getStringProperty(KEY_CHANNEL_LAYOUT);
    }

    public String getSampleAspectRatio() {
        return this.getStringProperty(KEY_SAMPLE_ASPECT_RATIO);
    }

    public String getDisplayAspectRatio() {
        return this.getStringProperty(KEY_DISPLAY_ASPECT_RATIO);
    }

    public String getAverageFrameRate() {
        return this.getStringProperty(KEY_AVERAGE_FRAME_RATE);
    }

    public String getRealFrameRate() {
        return this.getStringProperty(KEY_REAL_FRAME_RATE);
    }

    public String getTimeBase() {
        return this.getStringProperty(KEY_TIME_BASE);
    }

    public String getCodecTimeBase() {
        return this.getStringProperty(KEY_CODEC_TIME_BASE);
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

