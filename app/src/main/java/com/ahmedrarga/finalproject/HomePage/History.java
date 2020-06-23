package com.ahmedrarga.finalproject.HomePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class History {
    public ArrayList<Map<String, String>> array = new ArrayList<>();

    public History(){

    }
    public void set(int id, String mediaType) {
        if (array != null) {
            Map m = new HashMap();
            m.put("id", String.valueOf(id));
            m.put("media_type", mediaType);
            array.add(m);
        }
    }
}
