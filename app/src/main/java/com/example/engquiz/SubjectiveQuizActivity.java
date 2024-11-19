package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SubjectiveQuizActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private EditText editTextAnswer;
    private Button buttonSubmit;

    // 퀴즈 데이터 (예시로 5문제)
    private String[] questions = {"사과", "바나나", "포도", "오렌지", "수박"};
    private String[] answers = {"apple", "banana", "grape", "orange", "watermelon"};

    private int currentQuestionIndex = 0; // 현재 문제 번호
    private int score = 0; // 맞힌 문제 수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_quiz);

        // 뷰 초기화
        textViewQuestion = findViewById(R.id.textViewQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // 첫 번째 질문 설정
        textViewQuestion.setText(questions[currentQuestionIndex]);

        // 제출 버튼 클릭 시 정답 확인 및 다음 문제로 이동
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = editTextAnswer.getText().toString().trim(); //텍스트입력받고 문자열로 바꾸고 공백 제거
                checkAnswer(userAnswer);
            }
        });
    }

    // 정답 확인 메소드
    private void checkAnswer(String userAnswer) {
        if (userAnswer.equalsIgnoreCase(answers[currentQuestionIndex])) { //여기서 사용자가 입력한 정답과 저장되어 있는 단어를 대조
            Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
            score++; //점수
        } else {
            Toast.makeText(this, "오답입니다. 정답은 " + answers[currentQuestionIndex] + "입니다.", Toast.LENGTH_SHORT).show();
        }

        // 다음 문제로 이동하거나 결과 화면으로 이동
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            // 다음 질문 설정
            editTextAnswer.setText(""); // 입력 필드 초기화
            textViewQuestion.setText(questions[currentQuestionIndex]);
        } else {
            // 모든 문제를 풀었을 때 결과 화면으로 이동
            showResult();
        }
    }

    // 결과 화면으로 이동하는 메소드
    private void showResult() {
        Intent intent = new Intent(SubjectiveQuizActivity.this, SubjectiveResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", questions.length);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}
