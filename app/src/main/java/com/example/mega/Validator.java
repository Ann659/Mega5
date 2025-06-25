package com.example.mega;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Validator {
    private static final String EMAIL_ADDRESS = "^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$";
    private static final int MAX_GENERAL_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;

    public static boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validatePassword(String password) {
        return password != null && !password.isEmpty() && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean validateGeneralField(String field) {
        return field != null && !field.isEmpty() && field.length() <= MAX_GENERAL_LENGTH;
    }
    public static boolean validatePhone(String phone) {
        return phone.matches("^\\+7\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$");
    }
}