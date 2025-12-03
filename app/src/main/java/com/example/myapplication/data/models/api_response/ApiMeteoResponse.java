package com.example.myapplication.data.models.api_response;

import java.util.ArrayList;
import java.util.List;

public class ApiMeteoResponse {
    String message;
    WeatherData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WeatherData getData() {
        return data;
    }

    public void setData(WeatherData data) {
        this.data = data;
    }

    public static class WeatherData{
        List<Integer> precipitation_probability;
        List<String> forecast_time ;
        List<Integer> temperatures ;
        List<Double> precipitation;
        List<Double> wind_speed;
        List<Integer> humidity ;

        public List<Integer> getPrecipitation_probability() {
            return precipitation_probability != null ? precipitation_probability : new ArrayList<>();
        }

        public void setPrecipitation_probability(List<Integer> precipitation_probability) {
            this.precipitation_probability = precipitation_probability;
        }

        public List<String> getHourly_time() {
            return forecast_time != null ? forecast_time : new ArrayList<>();
        }

        public void setHourly_time(List<String> forecast_time) {
            this.forecast_time = forecast_time;
        }

        public List<Integer> getTemperatures() {
            return temperatures != null ? temperatures : new ArrayList<>();
        }

        public void setTemperatures(List<Integer> temperatures) {
            this.temperatures = temperatures;
        }

        public List<Double> getPrecipitation() {
            return precipitation != null ? precipitation : new ArrayList<>();
        }

        public void setPrecipitation(List<Double> precipitation) {
            this.precipitation = precipitation;
        }

        public List<Double> getWind_speed() {
            return wind_speed != null ? wind_speed : new ArrayList<>();
        }

        public void setWind_speed(List<Double> wind_speed) {
            this.wind_speed = wind_speed;
        }

        public List<Integer> getHumidity() {
            return humidity != null ? humidity : new ArrayList<>();
        }

        public void setHumidity(List<Integer> humidity) {
            this.humidity = humidity;
        }
    }
}
