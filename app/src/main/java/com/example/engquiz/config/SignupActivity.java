package com.example.engquiz.config;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.ApiService;
import com.example.engquiz.MainActivity;
import com.example.engquiz.R;
import com.example.engquiz.RetrofitClient;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    // 임시 데이터
//    private User user;

    EditText editTextUserid;
    EditText editTextPassword;
    EditText editTextNickname;

    Button signupButton;
    Button goMainButton;

    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        editTextUserid = findViewById(R.id.signup_userid);
        editTextPassword = findViewById(R.id.signup_password);
        editTextNickname = findViewById(R.id.signup_nickname);

        signupButton = findViewById(R.id.signup_button);
        goMainButton = findViewById(R.id.go_main_button);

        signupButton.setOnClickListener(v -> {

            String username = editTextUserid.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String nickname = editTextNickname.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(SignupActivity.this, "입력에 빈 값이 존재합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            processSignup(username,password,nickname);
            // use @NonNull, 예외 처리 x (if editText == null)
        });

        goMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void processSignup(String username, String password, String nickname) {
        ApiService apiService = RetrofitClient.getClient(null,this).create(ApiService.class);
        SignupRequest signupRequest = new SignupRequest(username, password, nickname);

        apiService.signup(signupRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 회원가입 성공
                    Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();

                    // 로그인 화면으로 이동
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // SignupActivity 종료
                } else {
                    // 회원가입 실패 처리
                    Toast.makeText(SignupActivity.this, "회원가입 실패: 이미 존재하는 사용자입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(SignupActivity.this, "네트워크 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
