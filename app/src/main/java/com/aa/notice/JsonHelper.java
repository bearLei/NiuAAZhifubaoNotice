package com.aa.notice;


import com.alibaba.fastjson.JSONObject;
import com.google.myjson.Gson;
import com.google.myjson.JsonElement;
import com.google.myjson.JsonObject;

import java.lang.reflect.Type;

public class JsonHelper {
    private static Gson sGson = new Gson();

    public static String toJson(Object o) {
        if (o == null) {
            return "";
        }
        return sGson.toJson(o);
    }


    public static JsonObject fromJson(Object json) {
        return fromJson(json, JsonObject.class);
    }

    public static <T> T fromJson(Object json, Class<T> tClass) {
        try {

            if (json == null) {
                return null;
            }
            if (json instanceof JsonElement) {
                return sGson.fromJson((JsonElement) json, tClass);
            }
            return sGson.fromJson(json.toString(), tClass);
        } catch (Throwable t) {

        }
        return null;
    }

    public static <T> T fromJson(Object json, Type tClass) {
        try {
            if (json == null) {
                return null;
            }
            if (json instanceof JsonElement) {
                return sGson.fromJson((JsonElement) json, tClass);
            }
            return sGson.fromJson(json.toString(), tClass);

        } catch (Throwable t) {

        }
        return null;
    }

    public static Gson getGson() {
        return sGson;
    }

    public static boolean isJson(String content) {

        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
