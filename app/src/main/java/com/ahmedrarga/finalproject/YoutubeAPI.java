package com.ahmedrarga.finalproject;

import org.json.JSONException;
import org.json.JSONObject;

public class YoutubeAPI {
    public static String api_key = "AIzaSyBS4wIJlcc-9pj_KEYhNBy84nKCJ4-ppaY";
    private JSONObject obj;
    private static String youtube = "https://www.youtube.com/watch?v=";

    public YoutubeAPI(JSONObject video){
        obj = video;
    }

    public String getId() {
        try {
            return obj.getString("key");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }

    }

    public String getTitle(){
        try {
            return obj.getString("name");
        }catch (JSONException e){
            System.out.println(e.getMessage());
            return "";
        }
    }
}
