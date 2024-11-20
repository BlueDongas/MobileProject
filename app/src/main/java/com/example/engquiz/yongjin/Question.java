package com.example.engquiz.yongjin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Question {
    // 문제
    private String question;
    // 정답지
    private List<String> options;
    // 정답
    private String answer;          

//    public Question(String question, List<String> options, String answer) {
//        this.question = question;
//        this.options = options;
//        this.answer = answer;
//    }


}
