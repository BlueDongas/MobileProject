package com.example.engquiz.sheunghoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.ApiService;
import com.example.engquiz.R;
import com.example.engquiz.RetrofitClient;
import com.example.engquiz.config.LoginActivity;
import com.example.engquiz.yongjin.Question;
import com.example.engquiz.yongjin.QuizActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectiveQuizActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private EditText editTextAnswer;
    private Button buttonSubmit;
    private TextView timerTextView; // 타이머를 표시할 TextView 추가
    private TextView progressTextView; // 진행 상황을 표시할 TextView 추가
    private ProgressBar progressBar; // 프로그레스 바 추가


    // 퀴즈 데이터 (예시로 5문제)
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0; // 현재 문제 번호
    private int score = 0; // 맞힌 문제 수
    private CountDownTimer countDownTimer; // 타이머 객체

    private static final int TIMER_START = 30000; // 타이머 시작 시간 (30초)
    private static final int TIMER_INTERVAL = 1000; // 타이머 업데이트 간격 (1초)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_quiz);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwtToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SubjectiveQuizActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else{
            Log.d("QuizActivity","JWT Token : "+token);
        }

        ApiService apiService = RetrofitClient.getClient(token,this ).create(ApiService.class);
        Intent Preintent = getIntent();
        int LV = Preintent.getIntExtra("LV",-1);

        // 뷰 초기화
        textViewQuestion = findViewById(R.id.textViewQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        timerTextView = findViewById(R.id.timer);
        progressTextView = findViewById(R.id.Txtprogress); // 진행 상황을 표시할 텍스트뷰 초기화
        progressBar = findViewById(R.id.progressBar); // 프로그레스바 초기화

        // 타이머 시작
        startTimer();

        fetchQuestions(apiService,LV);
        // 첫 번째 질문 설정

        // 제출 버튼 클릭 시 정답 확인 및 다음 문제로 이동
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = editTextAnswer.getText().toString().trim(); //텍스트입력받고 문자열로 바꾸고 공백 제거
                checkAnswer(userAnswer);

                // 타이머를 초기화하고 새로 시작
                if (countDownTimer != null) {
                    countDownTimer.cancel(); // 타이머 취소
                }
                startTimer(); // 새로 시작하는 타이머
                updateProgress(); // 진행 상황 업데이트

                textViewQuestion.setText(questionList.get(currentQuestionIndex).getKoreanWord());
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            finish(); // ESC 키 입력 시 Activity 종료
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void fetchQuestions(ApiService apiService, int level) {
        apiService.getWords(level).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();

                    textViewQuestion.setText(questionList.get(currentQuestionIndex).getKoreanWord());// 첫번째 질문 설정
                } else {
                    Toast.makeText(SubjectiveQuizActivity.this, "세션이 만료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Toast.makeText(SubjectiveQuizActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("QuizActivity", "API Error", t);
                finish();
            }
        });
    }

    private void updateProgress() {
        // 현재 문제 번호와 전체 문제 수를 바탕으로 프로그레스바 업데이트
        int progress = (int) (((float) currentQuestionIndex / questionList.size()) * 100);
        progressBar.setProgress(progress);

        // 진행 상황 텍스트 업데이트
        progressTextView.setText(String.format("%d/%d", currentQuestionIndex + 1, questionList.size()));
    }

    private void startTimer() {
        // 타이머 시작
        countDownTimer = new CountDownTimer(TIMER_START, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 남은 시간을 갱신
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                timerTextView.setText(String.format("남은 시간: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // 타이머가 끝났을 때 호출
                Toast.makeText(SubjectiveQuizActivity.this, "시간 초과! 다음 문제로 넘어갑니다.", Toast.LENGTH_SHORT).show();
                moveToNextQuestion();
            }
        };

        countDownTimer.start();
    }

    private void moveToNextQuestion() {
        // 다음 문제로 넘어가는 메소드
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            // 다음 문제 설정
            editTextAnswer.setText(""); // 입력 필드 초기화
            textViewQuestion.setText(questionList.get(currentQuestionIndex).getKoreanWord());
            startTimer(); // 타이머 리셋 및 시작
            updateProgress();
        } else {
            showResult(); // 모든 문제를 풀었으면 결과 화면으로 이동
        }

    }

    // 정답 확인 메소드
    private void checkAnswer(String userAnswer) {
        if (userAnswer.equalsIgnoreCase(questionList.get(currentQuestionIndex).getEnglishWord())) { //여기서 사용자가 입력한 정답과 저장되어 있는 단어를 대조
            Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
            score++; //점수
        } else {
            Toast.makeText(this, "오답입니다. 정답은 " + questionList.get(currentQuestionIndex).getEnglishWord() + "입니다.", Toast.LENGTH_SHORT).show();
        }

        // 다음 문제로 이동하거나 결과 화면으로 이동
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            // 다음 질문 설정
            editTextAnswer.setText(""); // 입력 필드 초기화
            textViewQuestion.setText(questionList.get(currentQuestionIndex).getKoreanWord());
        } else {
            // 모든 문제를 풀었을 때 결과 화면으로 이동
            showResult();
        }
    }

    // 결과 화면으로 이동하는 메소드
    private void showResult() {
        Intent intent = new Intent(SubjectiveQuizActivity.this, SubjectiveResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", questionList.size());
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티 종료 시 타이머를 취소
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
