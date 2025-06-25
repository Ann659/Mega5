package com.example.mega;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);

        Button nextButton = findViewById(R.id.nextButton2);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome2Activity.this, Welcome3Activity.class);
            startActivity(intent);
        });
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome2Activity.this, Login1Activity.class);
            startActivity(intent);
        });
    }
}
