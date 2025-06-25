package com.example.mega;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mega.supabase.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONException;

public class RegisterDetailsActivity extends AppCompatActivity {
    private SupabaseClient supabaseClient;
    private String email;
    private EditText etFullName, etPassword;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        supabaseClient = new SupabaseClient();
        email = getIntent().getStringExtra("email");

        EditText etPassword = findViewById(R.id.etPassword);
        EditText etFullName = findViewById(R.id.etFullName);
        ImageView back = findViewById(R.id.back);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();

            if (validateInputs(password, fullName)) {
                registerUser(email, password, fullName);
            }
        });
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterEmailActivity.class));
            finish();
        });
    }
    private boolean validateInputs(String password, String fullName) {
        fullName = etFullName.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (!Validator.validateGeneralField(fullName)) {
            etFullName.setError("Имя не должно иметь более 20 символов.");

        }

        if (!Validator.validatePassword(password)) {
            etPassword.setError("Пароль должен содержать хотя бы 8 символов.");

        }
        return true;
    }

    private void registerUser(String email, String password, String fullName) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Регистрация...");
        progressDialog.show();

        supabaseClient.createUser(email, password, fullName, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    try {
                        String userId = response.getJSONObject(0).getString("user_id");
                        // Save user data after registration
                        AuthManager authManager = AuthManager.getInstance();
                        authManager.saveUserData(userId, fullName, System.currentTimeMillis());

                        Toast.makeText(RegisterDetailsActivity.this,
                                "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterDetailsActivity.this, MainActivity3.class));
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(RegisterDetailsActivity.this,
                                "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterDetailsActivity.this,
                            "Ошибка регистрации: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
