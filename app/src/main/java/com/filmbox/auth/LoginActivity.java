package com.filmbox.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.filmbox.MainActivity;
import com.filmbox.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrPhone, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = new UserSession(this);
        if (userSession.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmailOrPhone = findViewById(R.id.etEmailOrPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(v -> performLogin());
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void performLogin() {
        String emailOrPhone = etEmailOrPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (emailOrPhone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userSession.loginUser(emailOrPhone, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials. Please try registering first.", Toast.LENGTH_SHORT).show();
        }
    }
}
