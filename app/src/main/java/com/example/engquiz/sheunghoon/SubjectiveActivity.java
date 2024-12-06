package com.example.engquiz.sheunghoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.R;

public class SubjectiveActivity extends AppCompatActivity {

    private Button startQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_main);

        Intent Preintent = getIntent();
        int LV = Preintent.getIntExtra("LV",-1);

        // 뷰 초기화
        startQuiz = findViewById(R.id.startQuiz);

        // 퀴즈 시작 버튼 클릭 시 QuizActivity로 이동
        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubjectiveActivity.this, SubjectiveQuizActivity.class);
                intent.putExtra("LV",LV);
                intent.putExtra("Quizflag","normal");
                startActivity(intent);
            }
        });
    }
}