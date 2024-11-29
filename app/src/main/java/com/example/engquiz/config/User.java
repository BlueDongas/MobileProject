package com.example.engquiz.config;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

// 회원가입 후 aws s3에 저장할 User Entity
@Data
@AllArgsConstructor
public class User {

    @NonNull
    Long id;

    @NonNull
    String userid;

    @NonNull
    String password;

    @NonNull
    String nickname;

}
