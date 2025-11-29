package com.example.myapplication.data.models.api_response;

public class ApiErrorResponse {
        String message,status;

        String status_code;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getMessage() {
            return message != null ? message : "Unknown Error, Please Contact Developer.";
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public String getStatus_code() {
            return status_code;
        }

        public void setStatus_code(String status_code) {
            this.status_code = status_code;
        }
    }


