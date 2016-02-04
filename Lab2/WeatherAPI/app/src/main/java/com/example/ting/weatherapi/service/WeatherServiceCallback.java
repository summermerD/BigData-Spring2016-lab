package com.example.ting.weatherapi.service;

import com.example.ting.weatherapi.data.Channel;

/**
 * Created by Ting on 2/3/16.
 */
public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);
    void serviceFailure(Exception exception);
}
