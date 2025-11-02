package com.example.myapplication.data.models.users;

public class UserNotificationSettingsRequest {
    boolean flood_alert_value,weather_updates_value,emergency_alert_value;

    public UserNotificationSettingsRequest(boolean flood_alert_value, boolean weather_updates_value, boolean emergency_alert_value) {
        this.flood_alert_value = flood_alert_value;
        this.weather_updates_value = weather_updates_value;
        this.emergency_alert_value = emergency_alert_value;
    }

    public boolean isFlood_alert_value() {
        return flood_alert_value;
    }

    public void setFlood_alert_value(boolean flood_alert_value) {
        this.flood_alert_value = flood_alert_value;
    }

    public boolean isWeather_updates_value() {
        return weather_updates_value;
    }

    public void setWeather_updates_value(boolean weather_updates_value) {
        this.weather_updates_value = weather_updates_value;
    }

    public boolean isEmergency_alert_value() {
        return emergency_alert_value;
    }

    public void setEmergency_alert_value(boolean emergency_alert_value) {
        this.emergency_alert_value = emergency_alert_value;
    }
}
