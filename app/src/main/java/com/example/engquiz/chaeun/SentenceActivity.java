package com.example.engquiz.chaeun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SentenceActivity extends AppCompatActivity {

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private TextView Qtxt;
    private TextView Examtxt;
    private int totalQuestions;
    private ArrayList<String> userAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_main);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwtToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SentenceActivity.this, LoginActivity.class);
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


        fetchQuestions(apiService,LV);

        Qtxt = findViewById(R.id.TxtQ);
        Examtxt = findViewById(R.id.TxtExp);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView TxtProgress = findViewById(R.id.Txtprogress);
        Button nextSentenceBtn = findViewById(R.id.nextSentenceBtn);
        Button prevSentenceBtn = findViewById(R.id.prevSentenceBtn);
        TextView timerTxt = findViewById(R.id.timerView);
        EditText answerInput = findViewById(R.id.answerInput);
        Button confirmBtn = findViewById(R.id.confirmBtn);
        Button quitBtn = findViewById(R.id.quitBtn); // 포기하기 버튼



        progressBar.setMax(totalQuestions);
        progressBar.setProgress(1);
        TxtProgress.setText("1/" + totalQuestions); // 남은 문제 로직?
        // 초기 값

        userAnswers = new ArrayList<>(); //사용자 입력값 저장 리스트

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
                intent.putExtra("Qlist", (CharSequence) questionList);// 조정 필요
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
                userAnswers.set(currentQuestionIndex, userInput);

                // Disable EditText for current question
                answerInput.setEnabled(false);
                Toast.makeText(getApplicationContext(), "답변 저장됨.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "단어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // Next question button
        nextSentenceBtn.setOnClickListener(view -> {
            if(userAnswers.get(currentQuestionIndex).isEmpty()){//답변을 저장하지 않았다면 다음 버튼 비활성화
                Toast.makeText(getApplicationContext(),"답변을 저장하세요!",Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentQuestionIndex < totalQuestions - 1) {
                currentQuestionIndex++;
                //다음 퀴즈 설정
                Qtxt.setText(questionList.get(currentQuestionIndex).getExample());
                Examtxt.setText(questionList.get(currentQuestionIndex).getExplanation());

                progressBar.setProgress(currentQuestionIndex + 1);
                TxtProgress.setText((currentQuestionIndex + 1) + "/" + totalQuestions);

                // Reset EditText for the next question
                answerInput.setText(userAnswers.get(currentQuestionIndex)); // Load previous answer if exists
                answerInput.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "마지막 질문입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Previous question button
        prevSentenceBtn.setOnClickListener(view -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                //이전 퀴즈
                Qtxt.setText(questionList.get(currentQuestionIndex).getExample());
                Examtxt.setText(questionList.get(currentQuestionIndex).getExplanation());

                progressBar.setProgress(currentQuestionIndex + 1);
                TxtProgress.setText((currentQuestionIndex + 1) + "/" + totalQuestions);

                // Reset EditText for the previous question
                answerInput.setText(userAnswers.get(currentQuestionIndex));
                answerInput.setEnabled(true);
            } else {
                Toast.makeText(getApplicationContext(), "첫 질문입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Quit button action
        quitBtn.setOnClickListener(view -> {
            timer.cancel(); // Cancel the timer
            moveToResult(questionList, userAnswers,currentQuestionIndex);
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
                    Qtxt.setText(questionList.get(currentQuestionIndex).getExample());
                    Examtxt.setText(questionList.get(currentQuestionIndex).getExplanation());
                    totalQuestions = questionList.size();

                    // Initialize answers list
                    for (int j = 0; j < totalQuestions; j++) {
                        userAnswers.add(""); // Default empty answers
                    }

                } else {
                    Toast.makeText(SentenceActivity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Toast.makeText(SentenceActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("QuizActivity", "API Error", t);
                finish();
            }
        });
    }
    // Helper method to move to result activity
    private void moveToResult(List<Question> Qlist, ArrayList<String> userAnswers, int currentIndex) {
        Intent intent = new Intent(getApplicationContext(), SentenceResultActivity.class);
        ArrayList<String> answer = new ArrayList<>();
        ArrayList<String> Queslist = new ArrayList<>();

        for (Question question:questionList){
            answer.add(question.getEnglishWord());
            Queslist.add(question.getExample());
        }
        intent.putStringArrayListExtra("answer",answer);
        intent.putStringArrayListExtra("Qlist",Queslist);
        intent.putExtra("userAnswers", userAnswers.toArray(new String[0]));
        intent.putExtra("currentIndex", currentIndex); // 현재 질문의 인덱스를 추가로 전달
        startActivity(intent);
        finish();
    }
}
