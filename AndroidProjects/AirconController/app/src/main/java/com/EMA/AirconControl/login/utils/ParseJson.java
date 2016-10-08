package com.EMA.AirconControl.login.utils;

import org.json.JSONArray;
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

    public static JSONObject getJsonStringFromJson(String jsonString, String keyString)
    {
        JSONObject msgJson = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            msgJson = jsonObject.getJSONObject(keyString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgJson;
    }

    public static String[] getStringArrayFromJson(String jsonString, String keyString)
    {
        String [] message = null;
        try {
            JSONArray tmpJsonArray;
            JSONObject jsonObject = new JSONObject(jsonString);
            tmpJsonArray = jsonObject.getJSONArray(keyString);
            message = new String[tmpJsonArray.length()];
            for(int i = 0; i < tmpJsonArray.length(); i++)
            {
               message[i] = tmpJsonArray.get(i).toString();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return message;
    }
    public static int getIntFromJson(String jsonString, String keyString)
    {
       int message = 0 ;
        try {

            JSONObject tmpJson = new JSONObject(jsonString);
            message = tmpJson.getInt(keyString);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return message;
    }

    public static JSONObject setStringToJson(String jsonString, String keyString, String message) {
        JSONObject tmpJson = null;
       try {
            tmpJson = new JSONObject(jsonString);
           tmpJson.put(keyString, message);

       }catch (Exception e)
       {
          e.printStackTrace();
       }
       return tmpJson;
    }

    public static JSONObject setBooleanToJson(String jsonString, String keyString, Boolean message) {
        JSONObject tmpJson = null;
        try {
            tmpJson = new JSONObject(jsonString);
            tmpJson.put(keyString, message);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return tmpJson;
    }

    public static JSONObject setIntToJson(String jsonString, String keyString, int message) {
        JSONObject tmpJson = null;
        try {
            tmpJson = new JSONObject(jsonString);
            tmpJson.put(keyString, message);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return tmpJson;
    }
}
