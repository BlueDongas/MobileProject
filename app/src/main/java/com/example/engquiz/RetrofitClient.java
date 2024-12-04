package com.example.engquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.engquiz.config.LoginActivity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://18.205.64.172:25000";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String token,Context context) {
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

                Response response = chain.proceed(requestBuilder.build());

                if (response.code() == 401){
                    handleTokenExpiry(context);
                }
                return response;
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

    private static void handleTokenExpiry(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().remove("jwtToken").apply();  // 토큰 삭제

        // 로그인 화면으로 이동
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}

