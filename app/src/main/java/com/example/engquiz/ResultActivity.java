package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 결과창 표시
        setContentView(R.layout.activity_result);

        TextView scoreText = findViewById(R.id.score_text);
        Button goMain = findViewById(R.id.go_main);

        // 점수 받아오기
        int score = getIntent().getIntExtra("score", 0);
        scoreText.setText("당신의 점수: " + score);

        goMain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
        });


    }
}
