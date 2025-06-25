package com.example.mega;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mega.supabase.SupabaseClient;

import org.json.JSONArray;

public class VerifyEmailActivity extends AppCompatActivity {
    private EditText etEmail, etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private Button btnContinue, btnSendCode;
    private TextView tvResendCode, tvTimer;
    private CountDownTimer countDownTimer;
    private String storedOtp;
    private SupabaseClient supabaseClient;
    private AuthManager authManager;
    private String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        authManager = AuthManager.getInstance();
        supabaseClient = new SupabaseClient();
        initViews();
        setupOtpFields();
    }

    private void fetchOtpCode(String email) {
        supabaseClient.getUserByEmail(email, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(JSONArray users) {
                runOnUiThread(() -> {
                    try {
                        if (users.length() == 0) {
                            showErrorDialog("Пользователь с таким email не найден");
                            resetVerification();
                            return;
                        }

                        storedOtp = users.getJSONObject(0).getString("otp_code");
                        new Handler().postDelayed(() -> {
                            Toast.makeText(VerifyEmailActivity.this,
                                    "Ваш OTP код: " + storedOtp, Toast.LENGTH_LONG).show();
                        }, 3000);
                    } catch (Exception e) {
                        Toast.makeText(VerifyEmailActivity.this,
                                "Ошибка сервера", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() ->
                        Toast.makeText(VerifyEmailActivity.this,
                                errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);
        btnContinue = findViewById(R.id.btnContinue);
        btnSendCode = findViewById(R.id.btnSendCode);
        tvResendCode = findViewById(R.id.tvResendCode);
        tvTimer = findViewById(R.id.tvTimer);
    }

    private void setupOtpFields() {
        setupOtpFieldNavigation(etOtp1, etOtp2);
        setupOtpFieldNavigation(etOtp2, etOtp3);
        setupOtpFieldNavigation(etOtp3, etOtp4);
        setupOtpFieldNavigation(etOtp4, etOtp5);
        setupOtpFieldNavigation(etOtp5, etOtp6);

        btnContinue.setOnClickListener(v -> verifyOtp());

        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (Validator.validateEmail(email)) {
                fetchOtpCode(email);
                toggleViewsVisibility(true);
                startTimer();
            } else {
                etEmail.setError("Введите корректный email");
            }
        });

        tvResendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (Validator.validateEmail(email)) {
                fetchOtpCode(email);
                startTimer();
            }
        });
    }

    private void setupOtpFieldNavigation(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    next.requestFocus();
                }
            }
        });
    }
    private void verifyOtp() {
        String enteredOtp = etOtp1.getText().toString() +
                etOtp2.getText().toString() +
                etOtp3.getText().toString() +
                etOtp4.getText().toString() +
                etOtp5.getText().toString() +
                etOtp6.getText().toString();

        if (enteredOtp.equals(storedOtp)) {
            navigateToMain();
        } else {
            showErrorDialog("Неверный код. Пожалуйста, проверьте код и попробуйте снова.");
        }
    }

    private void resetVerification() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        toggleViewsVisibility(false);
        storedOtp = null;
    }

    private void toggleViewsVisibility(boolean codeSent) {
        etEmail.setEnabled(!codeSent);
        btnSendCode.setVisibility(codeSent ? View.GONE : View.VISIBLE);
        findViewById(R.id.otpContainer).setVisibility(codeSent ? View.VISIBLE : View.GONE);
        findViewById(R.id.tvTimer).setVisibility(codeSent ? View.VISIBLE : View.GONE);
        btnContinue.setVisibility(codeSent ? View.VISIBLE : View.GONE);
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
                tvResendCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                tvResendCode.setEnabled(true);
                generatedOtp = null;
            }
        }.start();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToMain() {
        startActivity(new Intent(this, ChangePasswordActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}