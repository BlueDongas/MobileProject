package com.example.engquiz.chaeun;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.MainActivity;
import com.example.engquiz.R;

import java.util.ArrayList;

public class SentenceResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_result);

        // 시간 초과 토스트 메시지
        Toast.makeText(getApplicationContext(), "시간이 초과되었습니다!", Toast.LENGTH_SHORT).show();

        // 정답 리스트
        String[] AnswerList = {
                "went",     // She ___ to the store yesterday.
                "sitting",  // The cat is ___ on the mat.
                "read",     // I love to ___ books in my free time.
                "goes",     // He always ___ to work early in the morning.
                "host",     // They decided to ___ the party at their house.
                "play",     // The weather is perfect for ___ outside today.
                "lend",     // Can you ___ me your phone for a minute?
                "make",     // We need to ___ a decision quickly.
                "learn",    // She wants to ___ how to play the guitar.
                "painted"   // He ___ a beautiful painting for his art class.
        };

        // Intent 데이터 수신
        Intent intent = getIntent();
        String[] Qlist = intent.getStringArrayExtra("Qlist");
        String[] userAnswers = intent.getStringArrayExtra("userAnswers");

        Button goMain = findViewById(R.id.go_main);

        // 결과 리스트 생성
        ArrayList<SpannableString> strArray = new ArrayList<>();
        for (int i = 0; i < AnswerList.length; i++) {
            String question = Qlist[i];
            String correctAnswer = AnswerList[i];
            String userAnswer = userAnswers[i];

            String itemText = question + "\nYour Answer: " + userAnswer + "\nCorrect Answer: " + correctAnswer;

            SpannableString spannableString = new SpannableString(itemText);

            // 사용자의 답변이 틀린 경우 정답을 빨간색으로 표시
            if (!userAnswer.equals(correctAnswer)) {
                int startIndex = itemText.indexOf("Correct Answer: ") + "Correct Answer: ".length();
                int endIndex = itemText.length();
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, 0);
            }

            strArray.add(spannableString);
        }

        // ListView 설정
        ListView ltv = findViewById(R.id.listview);
        ArrayAdapter<SpannableString> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strArray);
        ltv.setAdapter(adapter);

        goMain.setOnClickListener(v -> {
            Intent i = new Intent(SentenceResultActivity.this, MainActivity.class);
            startActivity(i);
        });
    }
}
