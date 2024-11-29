package com.example.engquiz.config;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.MainActivity;
import com.example.engquiz.R;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    private User user;

    EditText editTextUserid;
    EditText editTextPassword;
    EditText editTextNickname;

    Button signupButton;

    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        editTextUserid = findViewById(R.id.signup_userid);
        editTextPassword = findViewById(R.id.signup_password);
        editTextNickname = findViewById(R.id.signup_nickname);

        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> {

            // signupButton 클릭시 입력한 회원 정보를 기반으로 User 생성
            // DB에 저장할 User data, 추후 필요시 DTO 생성하여 전달
            user = new User(
                    Math.abs(UUID.randomUUID().getMostSignificantBits()),
                    editTextUserid.getText().toString(),
                    editTextPassword.getText().toString(),
                    editTextNickname.getText().toString()
            );

            // use @NonNull, 예외 처리 x (if editText == null)

            Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT)
                    .show();

            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);

        });
    }
}
