/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  com.arthenica.smartexception.java.Exceptions
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.arthenica.ffmpegkit;

import android.util.Log;
import com.arthenica.ffmpegkit.Chapter;
import com.arthenica.ffmpegkit.MediaInformation;
import com.arthenica.ffmpegkit.StreamInformation;
import com.arthenica.smartexception.java.Exceptions;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaInformationJsonParser {
    public static final String KEY_STREAMS = "streams";
    public static final String KEY_CHAPTERS = "chapters";

    public static MediaInformation from(String ffprobeJsonOutput) {
        try {
            return MediaInformationJsonParser.fromWithError(ffprobeJsonOutput);
        }
        catch (JSONException e) {
            Log.e((String)"ffmpeg-kit", (String)String.format("MediaInformation parsing failed.%s", Exceptions.getStackTraceString((Throwable)e)));
            return null;
        }
    }

    public static MediaInformation fromWithError(String ffprobeJsonOutput) throws JSONException {
        JSONObject jsonObject = new JSONObject(ffprobeJsonOutput);
        JSONArray streamArray = jsonObject.optJSONArray(KEY_STREAMS);
        JSONArray chapterArray = jsonObject.optJSONArray(KEY_CHAPTERS);
        ArrayList<StreamInformation> streamList = new ArrayList<StreamInformation>();
        for (int i = 0; streamArray != null && i < streamArray.length(); ++i) {
            JSONObject streamObject = streamArray.optJSONObject(i);
            if (streamObject == null) continue;
            streamList.add(new StreamInformation(streamObject));
        }
        ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
        for (int i = 0; chapterArray != null && i < chapterArray.length(); ++i) {
            JSONObject chapterObject = chapterArray.optJSONObject(i);
            if (chapterObject == null) continue;
            chapterList.add(new Chapter(chapterObject));
        }
        return new MediaInformation(jsonObject, streamList, chapterList);
    }
}

