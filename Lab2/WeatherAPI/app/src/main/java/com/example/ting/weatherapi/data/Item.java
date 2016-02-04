package com.example.ting.weatherapi.data;

import org.json.JSONObject;

/**
 * Created by Ting on 2/3/16.
 */
public class Item implements JSONPopulator {
    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject("condition"));

    }
}
