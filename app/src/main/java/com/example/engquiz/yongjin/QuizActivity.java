package com.example.engquiz.yongjin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class QuizActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private QuizTimer quizTimer;

    private TextView questionText, timeText;
    private Button selection1, selection2, selection3, selection4, nextButton, prevButton, stopButton, checkAnswerButton;

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private boolean isPaused = false;

    private String selectedAnswer; // 사용자가 선택한 답변
    private Button clickedButton;  // 사용자가 클릭한 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        // UI 불러오기
        questionText = findViewById(R.id.question_text);
        // selection 1 ~ 4
        selection1 = findViewById(R.id.selection1);
        selection2 = findViewById(R.id.selection2);
        selection3 = findViewById(R.id.selection3);
        selection4 = findViewById(R.id.selection4);
        // 다음, 이전, 중단, 정답 확인 button
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        stopButton = findViewById(R.id.stop_button);
        checkAnswerButton = findViewById(R.id.check_answer_button);

        // timertext
        timeText = findViewById(R.id.progress_text);

        progressBar = findViewById(R.id.progress_bar);

        // QuizTimer 초기화 (30초 제한 시간)
        quizTimer = new QuizTimer(30000, timeText, progressBar);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://18.205.64.172:25000/") // Flask 서버 URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuizApi quizApi = retrofit.create(QuizApi.class);

        fetchQuestions(quizApi,1);

        // selection1~4 button 연결 동작 (공통)
        View.OnClickListener optionClickListener = view -> {
            clickedButton = (Button) view;
            // 선택한 button 가져오기
            selectedAnswer = clickedButton.getText().toString();

            // checkAnswerButton 활성화
            checkAnswerButton.setEnabled(true);
            checkAnswerButton.setVisibility(View.VISIBLE);
        };

        // 정답 확인 버튼 클릭 이벤트
        checkAnswerButton.setOnClickListener(view -> {
            Question currentQuestion = questionList.get(currentQuestionIndex);

            quizTimer.pause();

            if (selectedAnswer.equals(currentQuestion.getEnglishWord())) {
                // 정답일 경우
                clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                clickedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checking_button_true_ic, 0);
                score++;
            } else {
                // 오답일 경우
                clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                clickedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checking_button_false_ic, 0);
            }

            disableAllButtons();

        });

        selection1.setOnClickListener(optionClickListener);
        selection2.setOnClickListener(optionClickListener);
        selection3.setOnClickListener(optionClickListener);
        selection4.setOnClickListener(optionClickListener);

        
        // 다음 button 기능 정의
        nextButton.setOnClickListener(view -> {

            // quizTimer(제한시간 30초)가 전체 문제의 제한시간인지 아닌지 선택
            // 전체 제한시간 30초라면 아래의 quizTimer method 주석 처리
            quizTimer.reset();
            quizTimer.start();

            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                // 다음 quiz 이동
                quizDisplay();
            } else {
                // 결과 화면 이동
                // intent로 score 전송
                // name "score" 바꾸면 안돼
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }
        });

        // 이전 버튼 동작 정의
        prevButton.setOnClickListener(view -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                quizDisplay();
                quizTimer.reset();
                quizTimer.start();
            }
        });

        // 중단 버튼 동작 정의
        stopButton.setOnClickListener(view -> {
            if (!isPaused) {
                // 타이머 중단
                quizTimer.pause();
                isPaused = true; // 중단 상태로 전환
                stopButton.setText("재시작"); // 버튼 텍스트 변경
            } else {
                // 타이머 재시작
                quizTimer.start();
                isPaused = false; // 실행 상태로 전환
                stopButton.setText("중단"); // 버튼 텍스트 변경
            }
        });

    }

    // 임시로 만든 question, selection, answer(일단 명사만 해봄)
    // 문제를 영어로 제시하고 그 영어에 대한 문제에서 모르는 단어 같은 걸로 활용해서 다른 걸 만들어 볼까...?

    private void fetchQuestions(QuizApi quizApi, int level) {
        quizApi.getWords(level).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();
                    quizTimer.start(); // 타이머 시작
                    quizDisplay(); // 첫 번째 질문 표시
                } else {
                    Toast.makeText(QuizActivity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("QuizActivity", "API Error", t);
            }
        });
    }
    // 정답, 오답, 점수 추가 기능 만들어야함


    private void quizDisplay() {

        // questionList에서 list 불러오기
        Question currentQuestion = questionList.get(currentQuestionIndex);

        questionText.setText(currentQuestion.getKoreanWord());
        List<String> options = new ArrayList<>(4);
        options.add(currentQuestion.getEnglishWord()); //정답 옵션
        for (int i=0;i<3;i++){
            options.add("오답"+(i+1));
        }
        selection1.setText(options.get(0));
        selection2.setText(options.get(1));
        selection3.setText(options.get(2));
        selection4.setText(options.get(3));

        // 문제 넘어갈 때, button refresh
        refreshButton(selection1);
        refreshButton(selection2);
        refreshButton(selection3);
        refreshButton(selection4);

        // 첫번째 문제에서 이전 button 비활성화
        if (currentQuestionIndex == 0) {
            prevButton.setEnabled(false);
        } else {
            prevButton.setEnabled(true);
        }

        // checkAnswerButton 비활성화
//        checkAnswerButton.setVisibility(View.GONE);
        checkAnswerButton.setEnabled(false);
    }

//    // 정답 확인 후 점수
//    private void invalidAnswer(String selectedAnswer, Button userClickButton) {
//        // questionList quiz 가져오기
//        Question currentQuestion = questionList.get(currentQuestionIndex);
//        // 정답일 경우
//        if (selectedAnswer.equals(currentQuestion.getAnswer())) {
//            userClickButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
//            // 체크 아이콘
//            userClickButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checking_button_true_ic, 0);
//            score++;
//        } else {
//            // 오답일 경우
//            userClickButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
//            // x 아이콘
//            userClickButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checking_button_false_ic, 0);
//        }
//
//
////        // 다음 button 드러내기
////        nextButton.setVisibility(View.VISIBLE);
//    }

    // 정답, 오답의 체크 표시 이후 다음 문제로 넘어갈 때 button refresh 해야함
    private void refreshButton(Button refreshButton) {
        refreshButton.setEnabled(true);
        refreshButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        // 표시한 아이콘 refresh
        refreshButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    // 정답 확인 후 button 비활성화
    private void disableAllButtons() {
        selection1.setEnabled(false);
        selection2.setEnabled(false);
        selection3.setEnabled(false);
        selection4.setEnabled(false);
    }
}
