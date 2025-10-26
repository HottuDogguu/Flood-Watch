package com.example.myapplication.calbacks.auth;

import android.util.Log;

import com.example.myapplication.data.models.errors.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.Response;


public class APIResponseHandler {
    private static final Gson gson = new Gson();

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(Throwable error);
    }

    public static <T> void handleResponse(Response<T> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            callback.onSuccess(response.body());
        } else {
            handleErrorResponse(response, callback);
        }
    }

    private static <T> void handleErrorResponse(Response<T> response, ApiCallback<T> callback) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e("API_RESPONSE", "Error body: " + errorBody);
                Log.e("API_RESPONSE", "Status code: " + response.code());

                Exception error = parseError(errorBody, response.code());
                callback.onError(error);
            } else {
                callback.onError(new Exception("Unknown error with code: " + response.code()));
            }
        } catch (IOException e) {
            Log.e("API_RESPONSE", "Error reading error body", e);
            callback.onError(new Exception("Network error: " + e.getMessage()));
        }
    }

    private static Exception parseError(String errorBody, int statusCode) {
        try {
            // Try to parse as structured error
            ApiErrorResponse errorResponse = gson.fromJson(errorBody, ApiErrorResponse.class);

            String errorMessage = extractErrorMessage(errorResponse);
            return new Exception("Error: " + errorMessage + " (Status code: " + statusCode + ")");

        } catch (JsonSyntaxException e) {
            Log.e("API_RESPONSE", "Failed to parse error response", e);

            // Fallback: try to extract message from raw JSON
            String fallbackMessage = extractMessageFromRawJson(errorBody);
            return new Exception(fallbackMessage + " (Status: " + statusCode + ")");
        }
    }

    private static String extractErrorMessage(ApiErrorResponse errorResponse) {
        if (errorResponse == null) {
            return "Unknown error";
        }
        // Handle different error response structures
        if (errorResponse.getMessage() != null) {
            return errorResponse.getMessage();
        } else {
            return "Unknown error occurred";
        }
    }

    private static String extractMessageFromRawJson(String errorBody) {
        try {
            JsonObject jsonObject = gson.fromJson(errorBody, JsonObject.class);
            // Check common error message fields
            if (jsonObject.has("message")) return jsonObject.get("message").getAsString();
            if (jsonObject.has("error")) return jsonObject.get("error").getAsString();
            if (jsonObject.has("detail")) return jsonObject.get("detail").getAsString();
            if (jsonObject.has("status")) return jsonObject.get("status").getAsString();

        } catch (Exception e) {
            Log.e("API_RESPONSE", "Failed to extract message from raw JSON", e);
        }

        return errorBody.length() > 100 ? errorBody.substring(0, 100) + "..." : errorBody;
    }
}

