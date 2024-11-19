package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 이동 button 정의
    private Button goChaeun;
    private Button goSheunghoon;
    private Button goYongjin;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        // main 화면 표시
        setContentView(R.layout.activity_main);

        goChaeun = findViewById(R.id.go_chaeun_quiz);
        goSheunghoon = findViewById(R.id.go_shuenghoon_quiz);
        goYongjin = findViewById(R.id.go_yongjin_quiz);

        // 내가 구현한 거로 이동
        goYongjin.setOnClickListener(v -> {
            // Intent를 사용하여 화면 전환(data 전달)
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
        });

        // 채은이가 구현한 거로 이동
        goChaeun.setOnClickListener(v -> {
            // Intent를 사용하여 화면 전환(data 전달)
            Intent intent = new Intent(MainActivity.this, SentenceActivity.class);
            startActivity(intent);
        });

        goSheunghoon.setOnClickListener(v -> {
            // Intent를 사용하여 화면 전환(data 전달)
            Intent intent = new Intent(MainActivity.this, SubjectiveActivity.class);
            startActivity(intent);
        });
    }
}
