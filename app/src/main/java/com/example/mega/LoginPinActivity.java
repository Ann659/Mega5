package com.example.mega;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPinActivity extends AppCompatActivity {
    private EditText etPin1, etPin2, etPin3, etPin4;
    private Button btnContinue;
    private TextView tvForgotPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin);

        etPin1 = findViewById(R.id.etPin1);
        etPin2 = findViewById(R.id.etPin2);
        etPin3 = findViewById(R.id.etPin3);
        etPin4 = findViewById(R.id.etPin4);
        btnContinue = findViewById(R.id.btnContinue);
        tvForgotPin = findViewById(R.id.tvForgotPin);

        PinCodeTextWatcher pinWatcher = new PinCodeTextWatcher(etPin1, etPin2, etPin3, etPin4, btnContinue);
        etPin1.addTextChangedListener(pinWatcher);
        etPin2.addTextChangedListener(pinWatcher);
        etPin3.addTextChangedListener(pinWatcher);
        etPin4.addTextChangedListener(pinWatcher);

        tvForgotPin.setOnClickListener(v -> {
            startActivity(new Intent(this, VerifyEmailActivity.class));
        });

        btnContinue.setOnClickListener(v -> {
            String enteredPin = etPin1.getText().toString() +
                    etPin2.getText().toString() +
                    etPin3.getText().toString() +
                    etPin4.getText().toString();

            String savedPin = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .getString("user_pin", "");

            if (enteredPin.equals(savedPin)) {
                Log.d("PIN_AUTH", "PIN correct, navigating to MainActivity3");
                navigateToMain();
            } else {
                showErrorDialog("Неверный PIN-код. Пожалуйста, попробуйте снова.");
            }
        });
        TextView forgotPassword = findViewById(R.id.tvForgotPin);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPinActivity.this, VerifyEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
