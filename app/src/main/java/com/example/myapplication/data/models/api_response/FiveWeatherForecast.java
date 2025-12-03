package com.example.myapplication.data.models.api_response;

import java.util.List;

public class FiveWeatherForecast {
    int status_code;
    String status, message;
    List<HourlyWeatherForecast> data;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HourlyWeatherForecast> getData() {
        return data;
    }

    public void setData(List<HourlyWeatherForecast> data) {
        this.data = data;
    }

    public static class HourlyWeatherForecast {

        public HourlyWeatherForecast(String _id, String created_ad, String forecast_time,
                                     int precipitation_probability,int humidity,
                                     int temperature, double wind_speed, double precipitation) {
            this._id = _id;
            this.created_ad = created_ad;
            this.forecast_time = forecast_time;
            this.humidity = humidity;
            this.precipitation_probability = precipitation_probability;
            this.temperature = temperature;
            this.wind_speed = wind_speed;
            this.precipitation = precipitation;
        }
        public HourlyWeatherForecast(){

        }

        public HourlyWeatherForecast(int precipitation_probability, int humidity,
                                     int temperature, double wind_speed,
                                     double precipitation,String forecast_time) {
            this.precipitation_probability = precipitation_probability;
            this.humidity = humidity;
            this.forecast_time = forecast_time;
            this.temperature = temperature;
            this.wind_speed = wind_speed;
            this.precipitation = precipitation;
        }

        String _id, created_ad,forecast_time;
        int precipitation_probability,humidity,temperature;
        double wind_speed,precipitation;

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreated_ad() {
            return created_ad;
        }

        public void setCreated_ad(String created_ad) {
            this.created_ad = created_ad;
        }

        public String getForecast_time() {
            return forecast_time;
        }

        public void setForecast_time(String forecast_time) {
            this.forecast_time = forecast_time;
        }

        public int getPrecipitation_probability() {
            return precipitation_probability;
        }

        public void setPrecipitation_probability(int precipitation_probability) {
            this.precipitation_probability = precipitation_probability;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public double getWind_speed() {
            return wind_speed;
        }

        public void setWind_speed(double wind_speed) {
            this.wind_speed = wind_speed;
        }

        public double getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(double precipitation) {
            this.precipitation = precipitation;
        }
    }
}
