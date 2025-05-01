package com.epiis.detectaquinua.network;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageUploader {

    public interface UploadCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void uploadImage(File imageFile, UploadCallback callback) {
        OkHttpClient client = ApiClient.getClient();

        RequestBody fileBody = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", imageFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.18.26:8000/predict")  // Cambia por tu endpoint real
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onError("Error del servidor: " + response.code());
                }
            }
        });
    }
}

