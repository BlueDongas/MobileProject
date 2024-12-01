package com.example.engquiz.config;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    private String access_token; // 서버 응답의 키 값에 맞게 설정

    // Getter
    public String getAccessToken() {
        return access_token;
    }
}
