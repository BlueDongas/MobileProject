package com.example.engquiz.yongjin;

public class Question {
    private String english_word;   // 영어 단어
    private String korean_word;   // 한글 뜻
    private String example;       // 예문
    private String explanation;   // 설명

    private boolean isAnswered = false; // 정답 확인 여부
    private String selectedAnswer = null; // 선택된 답변

    public boolean isAnswered() { return isAnswered; }
    public void setAnswered(boolean answered) { isAnswered = answered; }
    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }

    // Getter 메서드
    public String getEnglishWord() { return english_word; }
    public String getKoreanWord() { return korean_word; }
    public String getExample() { return example; }
    public String getExplanation() { return explanation; }
}
