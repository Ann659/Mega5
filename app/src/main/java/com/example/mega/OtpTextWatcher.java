package com.example.mega;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class OtpTextWatcher implements TextWatcher {
    private final EditText[] editTexts;
    private final Button button;
    private int currentPosition = 0;

    public OtpTextWatcher(EditText et1, EditText et2, EditText et3,
                          EditText et4, EditText et5, EditText et6, Button button) {
        this.editTexts = new EditText[]{et1, et2, et3, et4, et5, et6};
        this.button = button;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        boolean allFieldsFilled = true;
        for (EditText editText : editTexts) {
            if (editText.getText().toString().isEmpty()) {
                allFieldsFilled = false;
                break;
            }
        }
        button.setEnabled(allFieldsFilled);

        if (s.length() == 1 && currentPosition < editTexts.length - 1) {
            currentPosition++;
            editTexts[currentPosition].requestFocus();
        } else if (s.length() == 0 && currentPosition > 0) {
            currentPosition--;
            editTexts[currentPosition].requestFocus();
            editTexts[currentPosition].setSelection(editTexts[currentPosition].getText().length());
        }
    }
}
