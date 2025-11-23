package com.example.myapplication.data.models.api_response;

import androidx.annotation.NonNull;

import java.util.List;

public class WebsocketResponseData {
    boolean is_flood_alert_on,is_weather_updates_on,is_emergency_alert_on, is_online_users_will_notify;
    Data data;

    public boolean isIs_flood_alert_on() {
        return is_flood_alert_on;
    }

    public boolean isIs_online_users_will_notify() {
        return is_online_users_will_notify;
    }

    public void setIs_online_users_will_notify(boolean is_online_users_will_notify) {
        this.is_online_users_will_notify = is_online_users_will_notify;
    }

    public void setIs_flood_alert_on(boolean is_flood_alert_on) {
        this.is_flood_alert_on = is_flood_alert_on;
    }

    public boolean isIs_weather_updates_on() {
        return is_weather_updates_on;
    }

    public void setIs_weather_updates_on(boolean is_weather_updates_on) {
        this.is_weather_updates_on = is_weather_updates_on;
    }

    public boolean isIs_emergency_alert_on() {
        return is_emergency_alert_on;
    }

    public void setIs_emergency_alert_on(boolean is_emergency_alert_on) {
        this.is_emergency_alert_on = is_emergency_alert_on;
    }

    public Data getData() {
        return data != null ? data  : new Data();
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        String topic, title, notification_text,value,severity,notification_created_at;
        List<Integer> precipitation_probability;
        List<String> forecast_time;
        List<Double> temperature;
        List<Double> precipitation;
        List<Double> wind_speed;
        List<Integer> humidity;

        public List<Integer> getHumidity() {
            return humidity;
        }

        public void setHumidity(List<Integer> humidity) {
            this.humidity = humidity;
        }

        public List<Integer> getPrecipitation_probability() {
            return precipitation_probability;
        }

        public void setPrecipitation_probability(List<Integer> precipitation_probability) {
            this.precipitation_probability = precipitation_probability;
        }

        public List<String> getHourly_time() {
            return forecast_time;
        }

        public void setHourly_time(List<String> forecast_time) {
            this.forecast_time = forecast_time;
        }

        public List<Double> getTemperatures() {
            return temperature;
        }

        public void setTemperatures(List<Double> temperature) {
            this.temperature = temperature;
        }

        public List<Double> getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(List<Double> precipitation) {
            this.precipitation = precipitation;
        }

        public List<Double> getWind_speed() {
            return wind_speed;
        }

        public void setWind_speed(List<Double> wind_speed) {
            this.wind_speed = wind_speed;
        }

        public String getNotification_created_at() {
            return notification_created_at;
        }

        public void setNotification_created_at(String notification_created_at) {
            this.notification_created_at = notification_created_at;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getValue() {
            return value != null ? value : "";
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTopic() {
            return topic != null ? topic : "";
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getTitle() {
            return title != null ? title : "";
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNotification_text() {
            return notification_text != null ? notification_text : "";
        }

        public void setNotification_text(String notification_text) {
            this.notification_text = notification_text;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "WebsocketResponseData{" +
                "is_flood_alert_on=" + is_flood_alert_on +
                ", is_weather_updates_on=" + is_weather_updates_on +
                ", is_emergency_alert_on=" + is_emergency_alert_on +
                ", is_online_users_will_notify=" + is_online_users_will_notify +
                ", data=" + data +
                '}';
    }
}
