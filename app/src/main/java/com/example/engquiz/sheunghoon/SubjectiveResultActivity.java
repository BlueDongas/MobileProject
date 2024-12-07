package com.example.engquiz.sheunghoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.MainActivity;
import com.example.engquiz.R;
import com.example.engquiz.config.LoginActivity;
import com.example.engquiz.yongjin.QuizActivity;

public class SubjectiveResultActivity extends AppCompatActivity {

    private TextView textViewScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_result);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwtToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SubjectiveResultActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else{
            Log.d("QuizActivity","JWT Token : "+token);
        }

        textViewScore = findViewById(R.id.textViewScore);

        ImageButton goMain = findViewById(R.id.go_main);

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