package com.example.engquiz.chaeun;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.R;

import java.util.ArrayList;

public class SentenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_main);

        String[] Qlist = {
                "1 She ___ to the store yesterday.",
                "2 The cat is ___ on the mat.",
                "3 I love to ___ books in my free time.",
                "4 He always ___ to work early in the morning.",
                "5 They decided to ___ the party at their house.",
                "6 The weather is perfect for ___ outside today.",
                "7 Can you ___ me your phone for a minute?",
                "8 We need to ___ a decision quickly.",
                "9 She wants to ___ how to play the guitar.",
                "10 He ___ a beautiful painting for his art class."
        };

        TextView Qtxt = findViewById(R.id.TxtQ);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView TxtProgress = findViewById(R.id.Txtprogress);
        Button nextSentenceBtn = findViewById(R.id.nextSentenceBtn);
        Button prevSentenceBtn = findViewById(R.id.prevSentenceBtn);
        TextView timerTxt = findViewById(R.id.timerView);
        EditText answerInput = findViewById(R.id.answerInput);
        Button confirmBtn = findViewById(R.id.confirmBtn);
        Button quitBtn = findViewById(R.id.quitBtn); // 포기하기 버튼

        int totalQuestions = Qlist.length;
        progressBar.setMax(totalQuestions);
        progressBar.setProgress(1);
        TxtProgress.setText("1/" + totalQuestions); // 남은 문제 로직?
        Qtxt.setText(Qlist[0]);

        final int[] currentIndex = {0};
        ArrayList<String> userAnswers = new ArrayList<>();

        // Initialize answers list
        for (int j = 0; j < totalQuestions; j++) {
            userAnswers.add(""); // Default empty answers
        }

        // Timer (60 seconds)
        final CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                timerTxt.setText("남은 시간: " + secondsRemaining + "초");
            }

            @Override
            public void onFinish() {
                // 전달 데이터 생성
                Intent intent = new Intent(getApplicationContext(), SentenceResultActivity.class);
                intent.putExtra("Qlist", Qlist);
                intent.putExtra("userAnswers", userAnswers.toArray(new String[0])); // 답변 배열 전달
                startActivity(intent);
                finish();
            }
        };

        // Start timer
        timer.start();

        // Confirm button to save answer
        confirmBtn.setOnClickListener(view -> {
            String userInput = answerInput.getText().toString().trim();

            if (!userInput.isEmpty()) {
                // Save current answer
                userAnswers.set(currentIndex[0], userInput);

                // Disable EditText for current question
                answerInput.setEnabled(false);
                Toast.makeText(getApplicationContext(), "답변 저장됨.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "단어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // Next question button
        nextSentenceBtn.setOnClickListener(view -> {
            if (currentIndex[0] < totalQuestions - 1) {
                currentIndex[0]++;
                Qtxt.setText(Qlist[currentIndex[0]]);
                progressBar.setProgress(currentIndex[0] + 1);
                TxtProgress.setText((currentIndex[0] + 1) + "/" + totalQuestions);

                // Reset EditText for the next question
                answerInput.setText(userAnswers.get(currentIndex[0])); // Load previous answer if exists
                answerInput.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "마지막 질문입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Previous question button
        prevSentenceBtn.setOnClickListener(view -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                Qtxt.setText(Qlist[currentIndex[0]]);
                progressBar.setProgress(currentIndex[0] + 1);
                TxtProgress.setText((currentIndex[0] + 1) + "/" + totalQuestions);

                // Reset EditText for the previous question
                answerInput.setText(userAnswers.get(currentIndex[0]));
                answerInput.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "첫 질문입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Quit button action
        quitBtn.setOnClickListener(view -> {
            timer.cancel(); // Cancel the timer
            moveToResult(Qlist, userAnswers,currentIndex[0]);
        });
    }
    // Helper method to move to result activity
    private void moveToResult(String[] Qlist, ArrayList<String> userAnswers,int currentIndex) {
        Intent intent = new Intent(getApplicationContext(), SentenceResultActivity.class);
        intent.putExtra("Qlist", Qlist);
        intent.putExtra("userAnswers", userAnswers.toArray(new String[0]));
        intent.putExtra("currentIndex", currentIndex); // 현재 질문의 인덱스를 추가로 전달
        startActivity(intent);
        finish();
    }
}
