package com.example.myapplication.data.models.api_response;

public class WebsocketResponseData {
    boolean is_flood_alert_on,is_weather_updates_on,is_emergency_alert_on;
    Data data;

    public boolean isIs_flood_alert_on() {
        return is_flood_alert_on;
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
        String topic, title, notification_text,value;

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
}
