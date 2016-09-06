package com.devilwwj.loginandregister.login.utils;

import org.json.JSONObject;

/*
 * Created by kylin on 16-8-27.
 */
public class ParseJson {

    public static boolean getBooleanFromJson(String jsonString, String keyString) {
        boolean state = false;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            state = jsonObject.getBoolean(keyString);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    public static String getStringFromJson(String jsonString, String keyString) {
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            message = jsonObject.getString(keyString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
}
