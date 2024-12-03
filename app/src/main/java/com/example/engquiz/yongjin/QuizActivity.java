package com.example.engquiz.yongjin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.ApiService;
import com.example.engquiz.MainActivity;
import com.example.engquiz.R;
import com.example.engquiz.RetrofitClient;
import com.example.engquiz.config.LoginActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuizActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private QuizTimer quizTimer;

    private TextView questionText, timeText, processText;
    private Button selection1, selection2, selection3, selection4, nextButton, prevButton, stopButton, checkAnswerButton;

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private boolean isPaused = false;

    // 선택한 답변과 고른 button
    private String selectedAnswer;
    private Button clickedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwtToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(QuizActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else{
            Log.d("QuizActivity","JWT Token : "+token);
        }

        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        //선택된 LV 받아오기
        Intent Preintent = getIntent();
        int LV = Preintent.getIntExtra("LV",-1);

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

        progressBar = findViewById(R.id.progress_bar);

//        progressBar.setMax(questionList.size());
//        progressBar.setProgress(1);

        // 남은 시간
        timeText = findViewById(R.id.timer_text);
        processText = findViewById(R.id.process_text);

//        progressBar = findViewById(R.id.progress_bar);

        // 수정 -> 전체 QuizTimer로 변경(5분 제한 시간)
        quizTimer = new QuizTimer(3000, timeText,this, score);
        quizTimer.start();


        fetchQuestions(apiService,LV);

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

            // 이 버튼으로 결과 산정하기 때문에 누른 즉시 비활성화
            checkAnswerButton.setEnabled(false);

            Question currentQuestion = questionList.get(currentQuestionIndex);

            // 이제는 중단할 필요가 없음
//            quizTimer.pause();

            currentQuestion.setAnswered(true);
            currentQuestion.setSelectedAnswer(selectedAnswer);

            if (selectedAnswer.equals(currentQuestion.getEnglishWord())) {
                // 정답일 경우
                clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                clickedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_true_icon, 0);
                score++;
            } else {
                // 오답일 경우
                clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                clickedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_false_icon, 0);
            }

            disableAllButtons();

        });

        selection1.setOnClickListener(optionClickListener);
        selection2.setOnClickListener(optionClickListener);
        selection3.setOnClickListener(optionClickListener);
        selection4.setOnClickListener(optionClickListener);

        // 다음 button 기능 정의
        nextButton.setOnClickListener(view -> {

            // 현재 문제가 정답 확인되지 않은 경우 경고 메시지 출력
            if (!questionList.get(currentQuestionIndex).isAnswered()) {
                Toast.makeText(this, "정답을 확인한 후에만 다음 문제로 이동할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
//            제한 시간을 전체 5분 설정
//            quizTimer.reset();
//            quizTimer.start();
            currentQuestionIndex++;

            if (currentQuestionIndex < questionList.size()) {
                // 다음 quiz 이동
                quizDisplay();

                // **진행률 업데이트**
                if (progressBar.getProgress() < progressBar.getMax()) {
                    progressBar.incrementProgressBy(1);
                    processText.setText(String.format("문제 %d/%d", progressBar.getProgress(), questionList.size()));
                }
            } else {

                // 결과 화면 이동
                // intent로 score 전송
                // name "score" 바꾸면 안돼
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
//              endQuiz();
            }
        });

        // 이전 버튼 동작 정의
        prevButton.setOnClickListener(view -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                quizDisplay();

                // **진행률 업데이트**
                if (progressBar.getProgress() > 0) {
                    progressBar.incrementProgressBy(-1);
                    processText.setText(String.format("문제 %d/%d", progressBar.getProgress(), questionList.size()));
                }
            }
        });

//        // 중단 버튼 동작 정의
//        stopButton.setOnClickListener(view -> {
//            if (!isPaused && quizTimer.getTimeRemaining() > 0) {
//                // 타이머 중단
//                quizTimer.pause();
//                isPaused = true;
//                stopButton.setText("재시작");
//            } else {
//                // 타이머 재시작
//                quizTimer.start();
//                isPaused = false;
//                stopButton.setText("중단");
//            }
//        });

        // 중단 button -> dialog로 재시작, 메인가기 정의
        stopButton.setOnClickListener(view -> {
            quizTimer.pause(); // 타이머 일시정지

            // dialog 연걸
            AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_stop_dialog, null);
            builder.setView(dialogView);

            AlertDialog alertDialog = builder.create();

            // dialog에서 사용할 button 정의
            Button restartButton = dialogView.findViewById(R.id.restart_button);
            Button mainMenuButton = dialogView.findViewById(R.id.main_menu_button);
            
//            restartButton.setText("재시작");
//            mainMenuButton.setText("메인으로");
            
            restartButton.setOnClickListener(v -> {
                quizTimer.start();
                alertDialog.dismiss(); // 다이얼로그 닫기
            });

            mainMenuButton.setOnClickListener(v -> {
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            });

            alertDialog.show();
        });


    }

    // 임시로 만든 question, selection, answer(일단 명사만 해봄)
    // 문제를 영어로 제시하고 그 영어에 대한 문제에서 모르는 단어 같은 걸로 활용해서 다른 걸 만들어 볼까...?

    private void fetchQuestions(ApiService apiService, int level) {
        apiService.getWords(level).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();

                    // ProgressBar 및 문제 현황 초기화
                    progressBar.setMax(questionList.size());
                    progressBar.setProgress(1);
                    // 진행률 업데이트
                    processText.setText(String.format("문제 %d/%d", currentQuestionIndex + 1, questionList.size()));

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
        // 정답지 shuffle인데 checkedAnswer를 눌렀을 때는 shuffle하지 않도록 할 수 있을 지도?
        Collections.shuffle(options);

        // 버튼 텍스트 설정 및 스타일 재적용
        setButtonStyle(selection1, options.get(0));
        setButtonStyle(selection2, options.get(1));
        setButtonStyle(selection3, options.get(2));
        setButtonStyle(selection4, options.get(3));

//        selection1.setText(options.get(0));
//        selection2.setText(options.get(1));
//        selection3.setText(options.get(2));
//        selection4.setText(options.get(3));
//
//        // 버튼에 스타일 적용
//        applyStyleToButton(selection1);
//        applyStyleToButton(selection2);
//        applyStyleToButton(selection3);
//        applyStyleToButton(selection4);

        // 문제 넘어갈 때, button refresh
        refreshButton(selection1);
        refreshButton(selection2);
        refreshButton(selection3);
        refreshButton(selection4);

        // 진행률 업데이트
//        processText.setText(String.format("문제 %d/%d", currentQuestionIndex + 1, questionList.size()));

        // 이미 정답을 확인한 문제일 때
        if (currentQuestion.isAnswered()) {
            disableAllButtons();
            String selectedAnswer = currentQuestion.getSelectedAnswer();
            Button selectedButton = getButtonByAnswer(selectedAnswer);

            if (selectedAnswer.equals(currentQuestion.getEnglishWord())) {
                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_true_icon, 0);
            } else {
                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_false_icon, 0);
            }
        }

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

    private Button getButtonByAnswer(String answer) {
        if (selection1.getText().toString().equals(answer)) return selection1;
        if (selection2.getText().toString().equals(answer)) return selection2;
        if (selection3.getText().toString().equals(answer)) return selection3;
        if (selection4.getText().toString().equals(answer)) return selection4;
        return null;
    }

    // 정답, 오답의 체크 표시 이후 다음 문제로 넘어갈 때 button refresh 해야함
    private void refreshButton(Button refreshButton) {
        refreshButton.setEnabled(true);
        refreshButton.setBackgroundResource(R.drawable.button_box1);
        // 표시한 아이콘 refresh
        refreshButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    // 정답 확인 후 button 비활성화를 위한
    private void disableAllButtons() {
        selection1.setEnabled(false);
        selection2.setEnabled(false);
        selection3.setEnabled(false);
        selection4.setEnabled(false);
    }

    // 스타일 적용 메서드
    // 아니 이거 backgroundResource, style 동시 적용 안돼 ;;;
    private void setButtonStyle(Button button, String text) {
        button.setText(text);
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.button_box1);
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
}