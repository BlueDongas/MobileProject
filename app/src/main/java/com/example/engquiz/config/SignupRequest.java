package com.example.engquiz.config;

public class SignupRequest {
    private String username; // 사용자 이름
    private String password; // 비밀번호
    private String nickname; // 별명

    public SignupRequest(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}

