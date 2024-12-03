package com.example.engquiz.config;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.ApiService;
import com.example.engquiz.MainActivity;
import com.example.engquiz.R;
import com.example.engquiz.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // signup 완료한 user data(실제 구현에서는 가져와야함 여기서는 임의의 data)
    private User user;

    EditText loginUserid;
    EditText loginPassword;

    Button loginButton;
    Button signupButton;
    Button goMainButton;

    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        loginUserid = findViewById(R.id.login_userid);
        loginPassword = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        goMainButton = findViewById(R.id.go_main_button);

        loginButton.setOnClickListener(v -> {
            String userId = loginUserid.getText().toString().trim(); // 입력된 ID 가져오기
            String password = loginPassword.getText().toString().trim(); // 입력된 비밀번호 가져오기

            // 입력 검증
            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "ID와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 로그인 요청 실행
            processLogin(userId, password);
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }
    private void processLogin(String userId, String password) {
        ApiService apiService = RetrofitClient.getClient(null,this  ).create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(userId, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken(); // 서버에서 받은 JWT 토큰

                    // JWT 토큰을 SharedPreferences에 저장
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("jwtToken",token);
                    editor.apply();

                    // 로그인 성공 메시지 표시
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                    // MainActivity로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // LoginActivity 종료
                } else {
                    // 로그인 실패 처리
                    Toast.makeText(LoginActivity.this, "로그인 실패: 아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(LoginActivity.this, "네트워크 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
