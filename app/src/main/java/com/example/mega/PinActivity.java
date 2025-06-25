package com.example.mega;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PinActivity extends AppCompatActivity {
    private EditText etPin1, etPin2, etPin3, etPin4;
    private Button btnContinue;
    private TextView tvClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);

        etPin1 = findViewById(R.id.etPin1);
        etPin2 = findViewById(R.id.etPin2);
        etPin3 = findViewById(R.id.etPin3);
        etPin4 = findViewById(R.id.etPin4);
        btnContinue = findViewById(R.id.btnContinue);
        tvClear = findViewById(R.id.tvClear);

        PinCodeTextWatcher pinWatcher = new PinCodeTextWatcher(etPin1, etPin2, etPin3, etPin4, btnContinue);
        etPin1.addTextChangedListener(pinWatcher);
        etPin2.addTextChangedListener(pinWatcher);
        etPin3.addTextChangedListener(pinWatcher);
        etPin4.addTextChangedListener(pinWatcher);

        tvClear.setOnClickListener(v -> clearPinFields());

        btnContinue.setOnClickListener(v -> {
            String pin = etPin1.getText().toString() +
                    etPin2.getText().toString() +
                    etPin3.getText().toString() +
                    etPin4.getText().toString();

            if (pin.length() == 4) {
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("user_pin", pin)
                        .apply();

                navigateToMain();
            }
        });
    }

    private void clearPinFields() {
        etPin1.setText("");
        etPin2.setText("");
        etPin3.setText("");
        etPin4.setText("");
        etPin1.requestFocus();
        btnContinue.setEnabled(false);
    }

    private void navigateToMain() {
        startActivity(new Intent(this, LoginPinActivity.class));
        finish();
    }

}
