package com.example.myapplication.data.models.api_response;

import java.util.ArrayList;
import java.util.List;

public class ApiUsesInformationResponse {

    String message;
    List<ApiSuccessfulResponse.UserData> data;
    PaginatedData paginated;

    public PaginatedData getPaginated() {
        return paginated != null ? paginated : new PaginatedData();
    }

    public void setPaginated(PaginatedData paginated) {
        this.paginated = paginated;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ApiSuccessfulResponse.UserData> getData() {
        return data != null ? data : new ArrayList<>();
    }

    public void setData(List<ApiSuccessfulResponse.UserData> data) {
        this.data = data;
    }

    public static class PaginatedData{
        int start_page, end_page, total_records;
        boolean has_next;

        public int getStart_page() {
            return start_page;
        }

        public void setStart_page(int start_page) {
            this.start_page = start_page;
        }

        public int getEnd_page() {
            return end_page;
        }

        public void setEnd_page(int end_page) {
            this.end_page = end_page;
        }

        public int getTotal_records() {
            return total_records;
        }

        public void setTotal_records(int total_records) {
            this.total_records = total_records;
        }

        public boolean isHas_next() {
            return has_next;
        }

        public void setHas_next(boolean has_next) {
            this.has_next = has_next;
        }
    }
}
