/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.arthenica.ffmpegkit;

import com.arthenica.ffmpegkit.Chapter;
import com.arthenica.ffmpegkit.StreamInformation;
import java.util.List;
import org.json.JSONObject;

public class MediaInformation {
    public static final String KEY_FORMAT_PROPERTIES = "format";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_FORMAT = "format_name";
    public static final String KEY_FORMAT_LONG = "format_long_name";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_SIZE = "size";
    public static final String KEY_BIT_RATE = "bit_rate";
    public static final String KEY_TAGS = "tags";
    private final JSONObject jsonObject;
    private final List<StreamInformation> streams;
    private final List<Chapter> chapters;

    public MediaInformation(JSONObject jsonObject, List<StreamInformation> streams, List<Chapter> chapters) {
        this.jsonObject = jsonObject;
        this.streams = streams;
        this.chapters = chapters;
    }

    public String getFilename() {
        return this.getStringFormatProperty(KEY_FILENAME);
    }

    public String getFormat() {
        return this.getStringFormatProperty(KEY_FORMAT);
    }

    public String getLongFormat() {
        return this.getStringFormatProperty(KEY_FORMAT_LONG);
    }

    public String getDuration() {
        return this.getStringFormatProperty(KEY_DURATION);
    }

    public String getStartTime() {
        return this.getStringFormatProperty(KEY_START_TIME);
    }

    public String getSize() {
        return this.getStringFormatProperty(KEY_SIZE);
    }

    public String getBitrate() {
        return this.getStringFormatProperty(KEY_BIT_RATE);
    }

    public JSONObject getTags() {
        return this.getFormatProperty(KEY_TAGS);
    }

    public List<StreamInformation> getStreams() {
        return this.streams;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
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

    public String getStringFormatProperty(String key) {
        JSONObject formatProperties = this.getFormatProperties();
        if (formatProperties == null) {
            return null;
        }
        if (formatProperties.has(key)) {
            return formatProperties.optString(key);
        }
        return null;
    }

    public Long getNumberFormatProperty(String key) {
        JSONObject formatProperties = this.getFormatProperties();
        if (formatProperties == null) {
            return null;
        }
        if (formatProperties.has(key)) {
            return formatProperties.optLong(key);
        }
        return null;
    }

    public JSONObject getFormatProperty(String key) {
        JSONObject formatProperties = this.getFormatProperties();
        if (formatProperties == null) {
            return null;
        }
        return formatProperties.optJSONObject(key);
    }

    public JSONObject getFormatProperties() {
        return this.jsonObject.optJSONObject(KEY_FORMAT_PROPERTIES);
    }

    public JSONObject getAllProperties() {
        return this.jsonObject;
    }
}

