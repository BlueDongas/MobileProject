package com.example.engquiz.yongjin;

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

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 결과창 표시
        setContentView(R.layout.activity_result);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwtToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else{
            Log.d("QuizActivity","JWT Token : "+token);
        }

        TextView scoreText = findViewById(R.id.score_text);
        ImageButton goMain = findViewById(R.id.go_main);

        // 점수 받아오기
        int score = getIntent().getIntExtra("score", 0);
        scoreText.setText("당신의 점수: " + score);

        goMain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
        });


    }
}
