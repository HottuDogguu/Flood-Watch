package com.example.myapplication.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultiPartUtility {

    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }

    public static MultipartBody.Part prepareFilePart(String partName, File file) {

        RequestBody fileBody =
                RequestBody.create(file, MediaType.parse("image/*"));

        return MultipartBody.Part.createFormData(
                partName,
                file.getName(),
                fileBody
        );
    }

    public static List<MultipartBody.Part> prepareImageList(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (files == null) return parts;

        for (File file : files) {
            if (file != null && file.exists()) {
                parts.add(prepareFilePart("images", file));
            }
        }

        return parts;
    }

    public static List<RequestBody> prepareTags(List<String> tags) {
        List<RequestBody> tagBodies = new ArrayList<>();
        if (tags == null) return tagBodies;

        for (String tag : tags) {
            tagBodies.add(toRequestBody(tag));
        }

        return tagBodies;
    }
}
