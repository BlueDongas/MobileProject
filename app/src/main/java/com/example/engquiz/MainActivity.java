package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.yongjin.QuizActivity;

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

        Spinner LVspinner = findViewById(R.id.LVspinner);
        String[] LVs = {"LV 1","LV 2","LV 3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,LVs);
        LVspinner.setAdapter(adapter);

        LVspinner.setSelection(0); // 초기 값 LV1

        LVspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {

                String selectedLV = parent.getItemAtPosition(position).toString();
                int level = Integer.parseInt(selectedLV.replaceAll("[^0-9]",""));
                // 내가 구현한 거로 이동
                goYongjin.setOnClickListener(v -> {
                    // Intent를 사용하여 화면 전환(data 전달)
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    intent.putExtra("LV",level);
                    startActivity(intent);
                });

                // 채은이가 구현한 거로 이동
                goChaeun.setOnClickListener(v -> {
                    // Intent를 사용하여 화면 전환(data 전달)
                    Intent intent = new Intent(MainActivity.this, SentenceActivity.class);
                    intent.putExtra("LV",level);
                    startActivity(intent);
                });

                goSheunghoon.setOnClickListener(v -> {
                    // Intent를 사용하여 화면 전환(data 전달)
                    Intent intent = new Intent(MainActivity.this, SubjectiveActivity.class);
                    intent.putExtra("LV",level);
                    startActivity(intent);
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this,"not selected LV",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
