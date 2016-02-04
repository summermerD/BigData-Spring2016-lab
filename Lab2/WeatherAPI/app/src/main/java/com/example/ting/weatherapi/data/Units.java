package com.example.ting.weatherapi.data;

import org.json.JSONObject;

/**
 * Created by Ting on 2/3/16.
 */
public class Units implements JSONPopulator {
    private  String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");

    }
}
