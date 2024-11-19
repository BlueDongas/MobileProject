package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QuizActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private QuizTimer quizTimer;

    private TextView questionText, timeText;
    private Button selection1, selection2, selection3, selection4, nextButton;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

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
        // 다음 button
        nextButton = findViewById(R.id.next_button);
        // timertext
        timeText = findViewById(R.id.progress_text);

        progressBar = findViewById(R.id.progress_bar);

        // QuizTimer 초기화 (30초 제한 시간)
        quizTimer = new QuizTimer(30000, timeText, progressBar);
        quizTimer.start();

        startQuiz();

        quizDisplay();

        // selection1~4 button 연결 동작 (공통)
        View.OnClickListener optionClickListener = view -> {
            Button clickedButton = (Button) view;
            // 선택한 button 가져오기
            String selectedAnswer = clickedButton.getText().toString();
            // 그 선택지의 정답 여부 확인
            invalidAnswer(selectedAnswer);
        };

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
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            }
        });
    }

    // 임시로 만든 question, selection, answer(일단 명사만 해봄)
    // 문제를 영어로 제시하고 그 영어에 대한 문제에서 모르는 단어 같은 걸로 활용해서 다른 걸 만들어 볼까...?
    private void startQuiz() {
        questionList = new ArrayList<>();

        questionList.add(new Question(
                "종이를 자르는 데 사용하는 도구는 무엇인가요?",
                Arrays.asList("Scissors", "Hammer", "Pencil", "Notebook"),
                "Scissors"
        ));

        questionList.add(new Question(
                "시간을 알려주는 장치는 무엇인가요?",
                Arrays.asList("Clock", "Phone", "Chair", "Pen"),
                "Clock"
        ));

        questionList.add(new Question(
                "날씨를 피하기 위해 사용하는 것은 무엇인가요?",
                Arrays.asList("Umbrella", "Shoes", "Bag", "Hat"),
                "Umbrella"
        ));
    }
    
    // 정답, 오답, 점수 추가 기능 만들어야함


    private void quizDisplay() {

        // questionList에서 list 불러오기
        Question currentQuestion = questionList.get(currentQuestionIndex);

        questionText.setText(currentQuestion.getQuestion());
        selection1.setText(currentQuestion.getOptions().get(0));
        selection2.setText(currentQuestion.getOptions().get(1));
        selection3.setText(currentQuestion.getOptions().get(2));
        selection4.setText(currentQuestion.getOptions().get(3));

        // 다음 button 감추기
        nextButton.setVisibility(View.GONE);
    }

    // 정답 확인 후 점수
    private void invalidAnswer(String selectedAnswer) {
        // questionList에서 현재 문제 가져오기
        Question currentQuestion = questionList.get(currentQuestionIndex);
        // 정답 맞으면 점수 올리기
        if (selectedAnswer.equals(currentQuestion.getAnswer())) {
            score++;
        }
        // 다음 button 드러내기
        nextButton.setVisibility(View.VISIBLE);
    }
}
