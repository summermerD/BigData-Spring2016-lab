package com.example.ting.weatherapi.service;

/**
 * Created by Ting on 2/3/16.
 */
public class YahooWeatherService {
    private WeatherServiceCallback callback;
    private String location;

    public YahooWeatherService(WeatherServiceCallback callback){
        this.callback = callback;
    }
    
    public void refreshWeather(String location){

    }
}
