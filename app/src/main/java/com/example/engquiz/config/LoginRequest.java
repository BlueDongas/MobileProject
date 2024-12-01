package com.example.engquiz.config;

public class LoginRequest {
    private String username;
    private String password;

    // 생성자
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

