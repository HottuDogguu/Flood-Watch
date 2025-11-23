package com.example.myapplication.data.models.api_response;

import java.util.ArrayList;
import java.util.List;

public class ListOfNotificationResponse {
    String message;
    int status_code;
    List<NotificationData> data;

    public List<NotificationData> getData() {
        return data != null? data : new ArrayList<>();
    }

    public void setData(List<NotificationData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message != null? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus_code() {
        return status_code ;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }



     public static class NotificationData{

         public NotificationData(String severity, String topic, String title, String notification_text, String created_at) {
             this.severity = severity;
             this.topic = topic;
             this.title = title;
             this.notification_text = notification_text;
             this.created_at = created_at;
         }

         String _id,severity,topic,title,notification_text,created_at;

         public String getSeverity() {
             return severity != null ? severity : "";
         }

         public void setSeverity(String severity) {
             this.severity = severity;
         }

         public String get_id() {
             return _id != null? _id : "";
         }

         public void set_id(String _id) {
             this._id = _id;
         }

         public String getTopic() {
             return topic != null? topic : "";
         }

         public void setTopic(String topic) {
             this.topic = topic;
         }

         public String getTitle() {
             return title != null? title : "";
         }

         public void setTitle(String title) {
             this.title = title;
         }

         public String getNotification_text() {
             return notification_text != null? notification_text : "";
         }

         public void setNotification_text(String notification_text) {
             this.notification_text = notification_text;
         }

         public String getCreated_at() {
             return created_at != null? created_at : "";
         }

         public void setCreated_at(String created_at) {
             this.created_at = created_at;
         }


     }
}
