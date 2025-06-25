package com.example.mega;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PhoneNumberTextWatcher implements TextWatcher {
    private boolean isFormatting;
    private final EditText editText;

    public PhoneNumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFormatting) return;

        if (s.length() > 0 && !s.toString().startsWith("+7")) {
            isFormatting = true;
            editText.setText("+7");
            editText.setSelection(2);
            isFormatting = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) return;
        isFormatting = true;

        String digits = s.toString().replaceAll("[^0-9]", "");
        StringBuilder formatted = new StringBuilder("+7");

        if (digits.length() > 1) {
            formatted.append("(").append(digits.substring(1, Math.min(4, digits.length())));
            if (digits.length() >= 4) {
                formatted.append(")").append(digits.substring(4, Math.min(7, digits.length())));
            }
            if (digits.length() >= 7) {
                formatted.append("-").append(digits.substring(7, Math.min(9, digits.length())));
            }
            if (digits.length() >= 9) {
                formatted.append("-").append(digits.substring(9, Math.min(11, digits.length())));
            }
        }

        editText.removeTextChangedListener(this);
        editText.setText(formatted.toString());
        editText.setSelection(Math.min(formatted.length(), editText.getText().length()));
        editText.addTextChangedListener(this);
        isFormatting = false;
    }
}
