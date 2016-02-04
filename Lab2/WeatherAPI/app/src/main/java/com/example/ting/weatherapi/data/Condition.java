package com.example.ting.weatherapi.data;

import org.json.JSONObject;

/**
 * Created by Ting on 2/3/16.
 */
public class Condition implements JSONPopulator {
    private int code;
    private int temperature;
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        code = data.optInt("code");
        temperature = data.optInt("temp");
        description = data.optString("text");

    }
}
