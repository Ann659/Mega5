package com.example.mega;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private TextInputLayout tilNewPassword, tilConfirmPassword;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        etNewPassword.addTextChangedListener(new PasswordTextWatcher());
        etConfirmPassword.addTextChangedListener(new PasswordTextWatcher());

        btnSaveChanges.setOnClickListener(v -> validateAndSavePassword());
    }

    private void validateAndSavePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validatePassword(newPassword, confirmPassword)) {
            saveUserData(newPassword);
            Toast.makeText(this, "Пароль успешно изменён", Toast.LENGTH_SHORT).show();
            navigateToHomeFragment();
        }
    }

    private void saveUserData(String newPassword) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("user_password", newPassword);

        editor.putString("user_name", "Иван Иванов");

        editor.putLong("registration_date", System.currentTimeMillis());

        editor.apply();
    }

    private void navigateToHomeFragment() {
        Intent intent = new Intent(this, MainActivity3.class);
        intent.putExtra("from_password_change", true);
        startActivity(intent);
        finish();
    }


    private boolean validatePassword(String newPass, String confirmPass) {
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Все поля должны быть заполнены");
            return false;
        }

        if (!newPass.equals(confirmPass)) {
            showError("Пароли не совпадают");
            return false;
        }

        if (newPass.length() < 8 || newPass.length() > 12) {
            showError("Пароль должен быть от 8 до 12 символов");
            return false;
        }

        if (Pattern.compile("[а-яА-Я]").matcher(newPass).find()) {
            showError("Пароль не должен содержать русские буквы");
            return false;
        }

        long digitCount = newPass.chars().filter(Character::isDigit).count();
        if (digitCount < 5) {
            showError("Пароль должен содержать минимум 5 цифр");
            return false;
        }

        long letterCount = newPass.chars().filter(c -> Character.isLetter(c) &&
                ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))).count();
        if (letterCount < 3) {
            showError("Пароль должен содержать минимум 3 английские буквы");
            return false;
        }
        if (newPass.isEmpty()) {
            tilNewPassword.setError("Введите пароль");
            return false;
        }
        if (confirmPass.isEmpty()) {
            tilConfirmPassword.setError("Подтвердите пароль");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveNewPassword(String newPassword) {
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .putString("user_password", newPassword)
                .apply();
    }

    private class PasswordTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tilNewPassword.setError(null);
            tilConfirmPassword.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
