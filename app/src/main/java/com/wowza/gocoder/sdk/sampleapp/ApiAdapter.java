package com.wowza.gocoder.sdk.sampleapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAdapter {

    private static final String BASE_URL = "http://gps.gistda.org:8080/api/";

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {

        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);

    }

    public static <S> S createService(Class<S> serviceClass, String newApiBaseUrl) {

        builder = new Retrofit.Builder()
                .baseUrl(newApiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson));

        return createService(serviceClass);

    }

}
