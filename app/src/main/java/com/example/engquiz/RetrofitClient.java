package com.example.engquiz;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://18.205.64.172:25000";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String token) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS);

        httpClient.addInterceptor(logging);

        if (token != null && !token.isEmpty()) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token); // JWT 토큰 추가
                return chain.proceed(requestBuilder.build());
            });
        }else{
            Log.e("RetrofitClient","JWT TOken is null or empty!");
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())  // 항상 새로 생성된 OkHttpClient 사용
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

