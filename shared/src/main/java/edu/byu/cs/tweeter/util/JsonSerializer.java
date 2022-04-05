package edu.byu.cs.tweeter.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonSerializer {

    public static String serialize(Object requestInfo) {
        return (new Gson()).toJson(requestInfo);
    }

    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

    public static <T> T deserialize(String value, Type returnType) {
        return (new Gson()).fromJson(value, returnType);
    }
}
