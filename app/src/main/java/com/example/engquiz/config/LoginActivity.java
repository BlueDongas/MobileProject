package com.example.engquiz.config;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.MainActivity;
import com.example.engquiz.R;

public class LoginActivity extends AppCompatActivity {

    // signup 완료한 user data(실제 구현에서는 가져와야함 여기서는 임의의 data)
    private User user;

    EditText loginUserid;
    EditText loginPassword;

    Button loginButton;

    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        loginUserid = findViewById(R.id.login_userid);
        loginPassword = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.login_button);

        // 임의의 user 생성
        user = new User(1L, "testId", "testPass", "testUsername");

        loginButton.setOnClickListener(v -> {
            // login을 위해 editText에 입력한 내용 가져오기
            String userId = loginUserid.getText().toString();
            String password = loginPassword.getText().toString();

            if (validate(userId, password)) {
                Toast.makeText(this, "로그인 성공! 환영합니다, " + user.getNickname() + "!", Toast.LENGTH_SHORT).show();
                // TODO: 다음 화면으로 이동하는 코드 추가
            } else {
                Toast.makeText(this, "로그인 실패! 아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
            
            // token based logic 작성
            // 유효한 token을 가지고 있다면 quiz 기능 이용 가능식의 logic

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        });
    }

    // 입력한 값과 DB에 저장된(현재는 임의의)값과 일치하는지
    private boolean validate(String userId, String password) {
         return user != null &&
                user.getUserid().equals(userId) &&
                user.getPassword().equals(password);
    }
}
