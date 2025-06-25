package com.example.mega;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Welcome1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome1);

        Button nextButton = findViewById(R.id.nextButton1);
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome1Activity.this, Welcome2Activity.class);
            startActivity(intent);
        });
    }
}