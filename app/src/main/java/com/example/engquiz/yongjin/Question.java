package com.example.engquiz.yongjin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class Question {
    private String english_word;   // 영어 단어
    private String korean_word;   // 한글 뜻
    private String example;       // 예문
    private String explanation;   // 설명

    // Getter 메서드
    public String getEnglishWord() { return english_word; }
    public String getKoreanWord() { return korean_word; }
    public String getExample() { return example; }
    public String getExplanation() { return explanation; }
}
