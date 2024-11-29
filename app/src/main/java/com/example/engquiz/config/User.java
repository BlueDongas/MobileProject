package com.example.engquiz.config;

import lombok.AllArgsConstructor;
import lombok.Data;

// 회원가입 후 aws s3에 저장할 User Entity
@Data
@AllArgsConstructor
public class User {

    Long id;

    String userid;

    String password;

    String username;

}
