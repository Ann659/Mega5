package com.example.mega;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import com.example.mega.supabase.SupabaseClient;

import java.util.regex.Pattern;

public class Login1Activity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AuthManager authManager;
    private SharedPreferences loginPrefs;
    private ImageView eyeIcon;
    private boolean isPasswordVisible = false;
    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthManager.init(getApplicationContext());
        setContentView(R.layout.activity_login);

        authManager = AuthManager.getInstance();
        loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        supabaseClient = new SupabaseClient();

        initViews();
        loadSavedCredentials();
        setupPasswordVisibilityToggle();
        setupClickListeners();
    }
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etEmail.setFocusableInTouchMode(true);
        etEmail.setFocusable(true);
        etEmail.setEnabled(true);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        eyeIcon = findViewById(R.id.eyeIcon);
        TextView tvForgotPassword = findViewById(R.id.forgot_password);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            Log.d("Navigation", "Attempting to navigate to RegisterEmailActivity");
            try {
                Intent intent = new Intent(Login1Activity.this, RegisterEmailActivity.class);
                startActivity(intent);
                Log.d("Navigation", "Activity started successfully");
            } catch (Exception e) {
                Log.e("Navigation", "Error starting activity", e);
            }
        });

        findViewById(R.id.forgot_password).setOnClickListener(v -> {
            startActivity(new Intent(this, VerifyEmailActivity.class));
        });
    }

    private void saveCredentials(String email, String password) {
        loginPrefs.edit()
                .putString("saved_email", email)
                .putString("saved_password", password)
                .apply();
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        saveCredentials(email, password);
        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        showLoading(true);

        supabaseClient.getUserByEmail(email, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(JSONArray users) {
                runOnUiThread(() -> {
                    showLoading(false);
                    try {
                        if (users.length() == 0) {
                            Toast.makeText(Login1Activity.this,
                                    "Пользователь не найден", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject user = users.getJSONObject(0);
                        String dbPassword = user.getString("password");
                        String otpCode = user.optString("otp_code", "");
                        String userId = user.getString("user_id");
                        String fullName = user.optString("full_name", "");

                        if (password.equals(dbPassword)) {
                            authManager.saveUserCredentials(email, password);
                            authManager.saveUserToken(userId);
                            authManager.saveOtpCode(otpCode);
                            authManager.saveUserData(userId, fullName, System.currentTimeMillis());

                            Toast.makeText(Login1Activity.this,
                                    "Вход выполнен", Toast.LENGTH_SHORT).show();
                            navigateToMain();
                        } else {
                            Toast.makeText(Login1Activity.this,
                                    "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(Login1Activity.this,
                                "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        Log.e("LoginError", "Error parsing user data", e);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(Login1Activity.this,
                            "Ошибка входа: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", errorMessage);
                });
            }
        });
    }
    private void showLoading(boolean show) {
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "Вход..." : "Войти");
    }

    private void setupPasswordVisibilityToggle() {
        eyeIcon.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            togglePasswordVisibility();
        });
        togglePasswordVisibility();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(null);
            eyeIcon.setImageResource(R.drawable.eye_opened);
        } else {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            eyeIcon.setImageResource(R.drawable.eye_closed);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void loadSavedCredentials() {
        String savedEmail = loginPrefs.getString("saved_email", "");
        String savedPassword = loginPrefs.getString("saved_password", "");
        etEmail.setText(savedEmail);
        etPassword.setText(savedPassword);
    }


    private boolean validateInputs(String email, String password) {
        if (!Validator.validateEmail(email)) {
            etEmail.setError("Неверный формат email");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Введите пароль");
            return false;
        }

        if (password.length() < 8 || password.length() > 12) {
            etPassword.setError("Пароль должен быть от 8 до 12 символов");
            return false;
        }

        if (Pattern.compile("[а-яА-Я]").matcher(password).find()) {
            etPassword.setError("Пароль не должен содержать русские буквы");
            return false;
        }

        long digitCount = password.chars().filter(Character::isDigit).count();
        if (digitCount < 5) {
            etPassword.setError("Пароль должен содержать минимум 5 цифр");
            return false;
        }

        long letterCount = password.chars().filter(c -> Character.isLetter(c) &&
                ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))).count();
        if (letterCount < 3) {
            etPassword.setError("Пароль должен содержать минимум 3 английские буквы");
            return false;
        }

        return true;
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, PinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }
}