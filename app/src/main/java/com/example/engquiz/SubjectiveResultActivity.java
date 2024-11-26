package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SubjectiveResultActivity extends AppCompatActivity {

    private TextView textViewScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_result);

        textViewScore = findViewById(R.id.textViewScore);

        Button goMain = findViewById(R.id.go_main);

        // 퀴즈 점수 받기
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        // 점수 표시
        textViewScore.setText("당신의 점수는 " + score + " / " + totalQuestions + "입니다.");

        goMain.setOnClickListener(v -> {
            Intent i = new Intent(SubjectiveResultActivity.this, MainActivity.class);
            startActivity(i);
        });


    }
}