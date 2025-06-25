package com.example.mega;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mega.supabase.SupabaseClient;

import org.json.JSONArray;

public class RegisterEmailActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnNext;
    private ImageView back;
    private Button btnContinue;
    private SupabaseClient supabaseClient;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        supabaseClient = new SupabaseClient();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> {
            email = etEmail.getText().toString().trim();
            if (Validator.validateEmail(email)) {
                supabaseClient.getUserByEmail(email, new SupabaseClient.SupabaseCallback() {
                    @Override
                    public void onSuccess(JSONArray users) {
                        runOnUiThread(() -> {
                            try {
                                if (users.length() > 0) {
                                    Toast.makeText(RegisterEmailActivity.this,
                                            "Этот email уже зарегистрирован", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(RegisterEmailActivity.this,
                                            RegisterDetailsActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                Toast.makeText(RegisterEmailActivity.this,
                                        "Ошибка проверки email", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() ->
                                Toast.makeText(RegisterEmailActivity.this,
                                        "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                etEmail.setError("Введите корректный email");
            }
        });

        back.setOnClickListener(v -> {
            startActivity(new Intent(this, Login1Activity.class));
            finish();
        });
    }

    private abstract static class TextValidator implements TextWatcher {
        private final EditText editText;

        public TextValidator(EditText editText) {
            this.editText = editText;
        }

        public abstract void validate(EditText editText, String text);

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            validate(editText, editText.getText().toString());
        }
    }
}