package com.example.engquiz.yongjin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import lombok.Data;

@Data
public class QuizTimer {

    // ProgressBar 시간 지날 때 마다 감소
    private CountDownTimer countDownTimer;
    // 총 시간
    private final long totalTime;
    // 남은 시간
    private long timeRemaining;

    private final TextView timerText;
//    private final ProgressBar progressBar;

    private TimerCallback callback;

    private int score;

    // context에 의존하여 데이터를 전달하고 싶지 않음
    private Context context;

    public QuizTimer(long totalTime, TextView timerText, Context context, int score) {
        this.totalTime = totalTime;
        this.timeRemaining = totalTime;
        this.timerText = timerText;
//        this.progressBar = progressBar;
        this.context = context;
        this.score = score;

//        // progressBar 초기 설정
//        progressBar.setMax((int) totalTime / 1000);
//        progressBar.setProgress(progressBar.getMax());
    }

    // 타이머 시작
    public void start() {
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;

                // ProgressBar 업데이트
                int secondsRemaining = (int) (millisUntilFinished / 1000);
//                progressBar.setProgress(secondsRemaining);

                // Timer 텍스트 업데이트
                timerText.setText("남은시간: " + secondsRemaining + "초");
            }

            @Override
            public void onFinish() {
                timerText.setText("시간 초과!");
//                progressBar.setProgress(0);


                // QuizActivity로부터 context를 받아와 ResultActivity로 이동
                // 여기서 context는 QuizActivity
                // QuizTimer는 Activity가 아니고 QuizActivity에 크게 의존하지만 Timer는 QuizActivity에 의존하는게 맞는 거 같음
                Intent intent = new Intent(context, ResultActivity.class);
                // "score" 그대로 받아오기(문자열 틀리면 못 받아 오더라..)
                intent.putExtra("score", score);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        };
        countDownTimer.start();
    }

    // 타이머 중단
    public void pause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface TimerCallback{

    }
}
